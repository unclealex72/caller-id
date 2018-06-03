package loader

import java.time.Clock

import call._
import cats.data.NonEmptyList
import com.typesafe.scalalogging.StrictLogging
import contact.{ContactDao, GoogleContactLoader, MongoDbContactDao}
import controllers.{AssetsComponents, Home}
import modem.{Modem, ModemSender, SendableAtzModem, TcpAtzModem}
import notify.Notifier
import notify.sinks.{LoggingSink, PersistingSink}
import number._
import play.api.ApplicationLoader
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.modules.reactivemongo.{ReactiveMongoApiComponents, ReactiveMongoApiFromContext}
import router.Routes

class AppComponents(context: ApplicationLoader.Context)
  extends ReactiveMongoApiFromContext(context)
    with AhcWSComponents
    with HttpFiltersComponents
    with ReactiveMongoApiComponents
    with AssetsComponents
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

  val validUsers: Seq[String] = configuration.get[String]("emails").split(',').map(_.trim).toSeq

  val contactLoader = new GoogleContactLoader(numberLocationService)
  val home = new Home(
    numberLocationService,
    persistedCallDao,
    contactLoader,
    contactService,
    maybeModemSender,
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
    assets
  )
}
