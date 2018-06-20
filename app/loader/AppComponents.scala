package loader

import java.time.{Clock, ZoneId}

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import auth._
import call._
import cats.data.NonEmptyList
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.crypto.{AuthenticatorEncoder, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.util.{FingerprintGenerator, PlayHTTPLayer, Clock => SilhouetteClock}
import notify.Notifier
import notify.sinks.{LoggingSink, PersistingSink, PushNotificationSink, SqueezeboxSink}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers.oauth2.GoogleProvider
import com.mohiva.play.silhouette.impl.providers.{DefaultSocialStateHandler, OAuth2Settings}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import com.typesafe.scalalogging.StrictLogging
import contact.{ContactDao, GoogleContactLoader, MongoDbContactDao}
import controllers.{AssetsComponents, DialogflowController, HomeController, SocialAuthController}
import dialogflow._
import loader.ConfigLoaders._
import modem.{Modem, ModemSender, SendableAtzModem, TcpAtzModem}
import number._
import play.api
import play.api.ApplicationLoader
import play.api.ConfigLoader._
import play.api.cache.ehcache.EhCacheComponents
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.filters.csrf.CSRFFilter
import play.filters.hosts.AllowedHostsFilter
import play.modules.reactivemongo.{ReactiveMongoApiComponents, ReactiveMongoApiFromContext}
import push._
import router.Routes
import squeezebox.{Squeezebox, SqueezeboxImpl}

import scala.concurrent.{ExecutionContext, Future}

class AppComponents(context: ApplicationLoader.Context)
  extends ReactiveMongoApiFromContext(context)
    with AhcWSComponents
    with HttpFiltersComponents
    with ReactiveMongoApiComponents
    with AssetsComponents
    with EhCacheComponents
    with StrictLogging {

  val cityDao: CityDao = CityDaoImpl()

  val mongoExecutionContext: ExecutionContext = actorSystem.dispatcher

  val locationConfiguration: LocationConfiguration = {
    val internationalDiallingCodes: Set[String] = cityDao.all().map(_.internationalDiallingCode).toSet
    val countryCode: String = configuration.getAndValidate[String]("location.countryCode", internationalDiallingCodes)
    val country: Country = cityDao.countries(countryCode).get.head
    val stdCodes: Set[String] = country.cities.map(_.stdCode).toSet
    val stdCode: String = configuration.getAndValidate[String]("location.stdCode", stdCodes)
    logger.info(s"Creating location configuration with international dialling code $countryCode and STD code $stdCode")
    val zoneId: ZoneId = configuration.getOptional[ZoneId]("location.timezone").getOrElse(ZoneId.systemDefault())
    LocationConfiguration(countryCode, stdCode, zoneId)
  }
  val localService: LocalService = new LocalServiceImpl(locationConfiguration.internationalCode, locationConfiguration.stdCode)
  val numberFormatter: NumberFormatter = new NumberFormatterImpl(localService)
  val numberLocationService: PhoneNumberFactory = new PhoneNumberFactoryImpl(cityDao, numberFormatter, localService)

  val clock: Clock = Clock.systemDefaultZone()
  val contactService: ContactDao = new MongoDbContactDao(reactiveMongoApi)(mongoExecutionContext)
  val callService: CallService = new CallServiceImpl(clock, numberLocationService, contactService)

  val callDao: CallDao = new MongoDbCallDao(reactiveMongoApi)(mongoExecutionContext)
  val (modem: Modem, maybeModemSender: Option[ModemSender]) = {
    val modemConfiguration: ModemConfiguration = configuration.get[ModemConfiguration]("modem")
    modemConfiguration match {
      case DebugModemConfiguration =>
        val modem = new SendableAtzModem()(actorSystem, materializer)
        (modem, Some(modem))
      case NetworkModemConfiguration(host, port) =>
        (new TcpAtzModem(host, port)(actorSystem, materializer), None)
    }
  }

  val userDao = new MongoDbUserDao(reactiveMongoApi)(mongoExecutionContext)
  val pushEndpointDao: PushEndpointDao = new MongoDbPushEndpointDao(reactiveMongoApi)
  val browserPushService: BrowserPushService = new BrowserPushServiceImpl(
    configuration.get[BrowserPushConfiguration]("push"),
    pushEndpointDao)
  val userService = new UserServiceImpl(userDao)
  val httpLayer = new PlayHTTPLayer(wsClient)
  val socialStateSigner = new JcaSigner(configuration.get[JcaSignerSettings]("silhouette.socialStateHandler.signer"))
  val socialStateHandler = new DefaultSocialStateHandler(Set(), socialStateSigner)
  val googleProvider = new GoogleProvider(httpLayer, socialStateHandler, configuration.get[OAuth2Settings]("silhouette.google"))
  val crypter = new JcaCrypter(configuration.get[JcaCrypterSettings]("silhouette.authenticator.crypter"))
  val idGenerator = new SecureRandomIDGenerator()
  val silhouetteClock = SilhouetteClock()
  val eventBus = EventBus()
  val cookieHeaderEncoding: CookieHeaderEncoding = new DefaultCookieHeaderEncoding()
  val authenticatorEncoder: AuthenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)
  val fingerprintGenerator: FingerprintGenerator = new DefaultFingerprintGenerator()
  val cookieAuthenticatorSettings: CookieAuthenticatorSettings =
    configuration.get[CookieAuthenticatorSettings]("silhouette.cookie")
  val authenticatorService =  new CookieAuthenticatorService(cookieAuthenticatorSettings, None,
    socialStateSigner, cookieHeaderEncoding, authenticatorEncoder, fingerprintGenerator, idGenerator, silhouetteClock)
  val env: Environment[DefaultEnv] = Environment[DefaultEnv](
    userService,
    authenticatorService,
    Seq(),
    eventBus
  )
  val bodyParserDefault = new api.mvc.BodyParsers.Default(playBodyParsers)
  val validUsers: Seq[String] = configuration.get[String]("silhouette.emails").split(',').map(_.trim)
  val authorization: Authorization[User, CookieAuthenticator] = new ValidUserAuthorization(validUsers)
  val unsecuredErrorHandler = new DefaultUnsecuredErrorHandler(messagesApi)
  val unsecuredRequestHandler = new DefaultUnsecuredRequestHandler(unsecuredErrorHandler)
  val unsecuredAction = new DefaultUnsecuredAction(unsecuredRequestHandler, bodyParserDefault)
  val userAwareRequestHandler = new DefaultUserAwareRequestHandler()
  val userAwareAction = new DefaultUserAwareAction(userAwareRequestHandler, bodyParserDefault)
  val authInfoDao = new MongoDbOauth2AuthInfoDao(reactiveMongoApi)(mongoExecutionContext)
  val authInfoRepository = new DelegableAuthInfoRepository(authInfoDao)
  val securedErrorHandler: SecuredErrorHandler = new DefaultSecuredErrorHandler(messagesApi) {
    override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = {
      socialAuthController.authenticate().apply(request).run()
    }
  }
  val securedRequestHandler = new DefaultSecuredRequestHandler(securedErrorHandler)
  val securedAction = new DefaultSecuredAction(securedRequestHandler, bodyParserDefault)
  val socialAuthController = new SocialAuthController(
    controllerComponents,
    env,
    userAwareAction,
    userService,
    authInfoRepository,
    googleProvider,
    defaultCacheApi)
  val silhouette: Silhouette[DefaultEnv] =
    new SilhouetteProvider[DefaultEnv](env, securedAction, unsecuredAction, userAwareAction)
  val contactLoader = new GoogleContactLoader(numberLocationService)
  val homeController = new HomeController(
    numberLocationService,
    numberFormatter,
    callDao,
    contactLoader,
    contactService,
    maybeModemSender,
    browserPushService,
    silhouette,
    authorization,
    authInfoDao,
    assets,
    controllerComponents)

  val callToSpeechService = new CallToSpeechServiceImpl(WebhookResponseDateTimeFormatter(), locationConfiguration.zoneId)
  val queryParameterService = new QueryParameterServiceImpl(clock)
  val dialogflowService = new DialogflowServiceImpl(queryParameterService,
    callToSpeechService,
    callDao)
  val dialogflowToken: String = configuration.get[String]("dialogflow.token")
  val dialogflowController = new DialogflowController(dialogflowService, dialogflowToken, controllerComponents)
  val networkSqueezeboxConfiguration: NetworkSqueezeboxConfiguration = configuration.get[NetworkSqueezeboxConfiguration]("squeezebox")
  val squeezebox: Squeezebox = new SqueezeboxImpl(networkSqueezeboxConfiguration.duration)

  val notifier: Notifier = {
    implicit val _ac: ActorSystem = actorSystem
    val loggingSink = new LoggingSink
    val persistingSink = new PersistingSink(callDao)
    val pushNotificationSink = new PushNotificationSink(browserPushService)
    val flowFactory: () => Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] = () =>
      Tcp().outgoingConnection(networkSqueezeboxConfiguration.host, networkSqueezeboxConfiguration.port)
    val squeezeboxSink = new SqueezeboxSink(squeezebox, flowFactory)
    new Notifier(
      modem,
      callService,
      applicationLifecycle,
      NonEmptyList.of(
        loggingSink,
        persistingSink,
        pushNotificationSink,
        squeezeboxSink
      ))
  }

  override def router: Router = new Routes(
    httpErrorHandler,
    homeController,
    dialogflowController,
    socialAuthController,
    assets
  )

  override def httpFilters: Seq[EssentialFilter] = {
    super.httpFilters.filterNot(_.isInstanceOf[AllowedHostsFilter]).filterNot(_.isInstanceOf[CSRFFilter])
  }
}
