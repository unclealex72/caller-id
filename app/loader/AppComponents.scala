package loader

import java.time.Clock

import notify.Notifier
import notify.sinks.{LoggingSink, PersistingSink}
import loader.ConfigLoaders._
import auth._
import call._
import cats.data.NonEmptyList
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.crypto.{AuthenticatorEncoder, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.util.{FingerprintGenerator, PlayHTTPLayer, Clock => SilhouetteClock}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers.oauth2.GoogleProvider
import com.mohiva.play.silhouette.impl.providers.{DefaultSocialStateHandler, OAuth2Settings, SocialProviderRegistry}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import com.typesafe.scalalogging.StrictLogging
import contact.{ContactDao, GoogleContactLoader, MongoDbContactDao}
import controllers.{AssetsComponents, Home, SocialAuthController}
import modem.{Modem, ModemSender, SendableAtzModem, TcpAtzModem}
import number._
import play.api
import play.api.ApplicationLoader
import play.api.ConfigLoader._
import play.api.cache.ehcache.EhCacheComponents
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.{CookieHeaderEncoding, DefaultCookieHeaderEncoding, RequestHeader, Result}
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.modules.reactivemongo.{ReactiveMongoApiComponents, ReactiveMongoApiFromContext}
import router.Routes

import scala.concurrent.Future

class AppComponents(context: ApplicationLoader.Context)
  extends ReactiveMongoApiFromContext(context)
    with AhcWSComponents
    with HttpFiltersComponents
    with ReactiveMongoApiComponents
    with AssetsComponents
    with EhCacheComponents
    with StrictLogging {

  val cityDao: CityDao = CityDaoImpl()

  implicit val locationConfiguration: LocationConfiguration = {
    val internationalDiallingCodes: Set[String] = cityDao.all().map(_.internationalDiallingCode).toSet
    val countryCode: String = configuration.getAndValidate[String]("location.countryCode", internationalDiallingCodes)
    val country: Country = cityDao.countries(countryCode).get.head
    val stdCodes: Set[String] = country.cities.map(_.stdCode).toSet
    val stdCode: String = configuration.getAndValidate[String]("location.stdCode", stdCodes)
    logger.info(s"Creating location configuration with international dialling code $countryCode and STD code $stdCode")
    LocationConfiguration(countryCode, stdCode)
  }
  val numberFormatter: NumberFormatter = new NumberFormatterImpl()
  val numberLocationService: NumberLocationService = new NumberLocationServiceImpl(cityDao, locationConfiguration)

  val clock: Clock = Clock.systemDefaultZone()
  val contactService: ContactDao = new MongoDbContactDao(reactiveMongoApi)
  val callService: CallService = new CallServiceImpl(clock, numberLocationService, contactService)

  val persistedCallDao: PersistedCallDao = new MongoDbPersistedCallDao(reactiveMongoApi)
  val persistedCallFactory: PersistedCallFactory = new PersistedCallFactoryImpl(numberFormatter)
  val (modem: Modem, maybeModemSender: Option[ModemSender]) = {
    val debug = configuration.get[Boolean]("modem.debug")
    if (debug) {
      logger.info("Debug modem enabled.")
      val modem = new SendableAtzModem()(actorSystem, materializer)
      (modem, Some(modem))
    }
    else {
      val host = configuration.get[String]("modem.host")
      val port = configuration.get[Int]("modem.port")
      (new TcpAtzModem(host, port)(actorSystem, materializer), None)
    }
  }

  val userDao = new MongoDbUserDao(reactiveMongoApi)
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
  val authInfoDao = new MongoDbOauth2AuthInfoDao(reactiveMongoApi)
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
  val home = new Home(
    numberLocationService,
    persistedCallDao,
    contactLoader,
    contactService,
    maybeModemSender,
    silhouette,
    authorization,
    authInfoDao,
    controllerComponents)


  val notifier: Notifier = {
    val loggingSink = new LoggingSink(numberFormatter)
    val persistingSink = new PersistingSink(persistedCallFactory, persistedCallDao)
    new Notifier(
      modem,
      callService,
      applicationLifecycle,
      NonEmptyList.of(
        loggingSink,
        persistingSink
      ))(actorSystem, materializer, executionContext)
  }

  override def router: Router = new Routes(
    httpErrorHandler,
    home,
    socialAuthController,
    assets
  )
}
