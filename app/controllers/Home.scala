package controllers

import java.time.Instant

import auth.{DefaultEnv, User}
import call.{Call => _, _}
import cats.data.Validated._
import cats.data._
import cats.implicits._
import com.mohiva.play.silhouette.api.{Authorization, Silhouette}
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.AuthInfoDAO
import contact.{Contact, ContactDao, ContactLoader}
import modem.ModemSender
import number.{FormattableNumber, NumberFormatter, NumberLocationService, PhoneNumber}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Filters => _, _}
import push.{BrowserPushService, PushSubscription}

import scala.concurrent.{ExecutionContext, Future}

class Home(
            val numberLocationService: NumberLocationService,
            val numberFormatter: NumberFormatter,
            val persistedCallDao: PersistedCallDao,
            val contactLoader: ContactLoader,
            val contactService: ContactDao,
            val maybeModemSender: Option[ModemSender],
            val browserPushService: BrowserPushService,
            val silhouette: Silhouette[DefaultEnv],
            val authorization: Authorization[DefaultEnv#I, DefaultEnv#A],
            val authInfoDao: AuthInfoDAO[OAuth2Info],
            val assets: Assets,
            override val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext)
  extends AbstractController(controllerComponents) {

  def unauthorised = Action { implicit request =>
    Forbidden(views.html.goaway())
  }

  def index = silhouette.SecuredAction(authorization).async { implicit request =>
    def build(persistedPhoneNumber: PersistedPhoneNumber, builder: PhoneNumber => CallView): Option[CallView] = {
      numberLocationService.decompose(persistedPhoneNumber.normalisedNumber) match {
        case Valid(phoneNumber) => Some(builder(phoneNumber))
        case Invalid(_) => None
      }
    }
    request.identity.fullName match {
      case Some(name) =>
        persistedCallDao.calls(max = Some(20)).map { persistedCalls =>
          val calls: Seq[CallView] = persistedCalls.flatMap { persistedCall =>
            val when: Instant = persistedCall.when
            persistedCall.caller match {
              case PersistedWithheld =>
                Some(CallView(when, None, None))
              case PersistedKnown(contactName, phoneType, maybeAvatarUrl, persistedPhoneNumber) =>
                build(persistedPhoneNumber, pn =>
                  CallView(when, Some(Contact(pn.normalisedNumber, contactName, phoneType, maybeAvatarUrl)), None))
              case PersistedUnknown(persistedPhoneNumber) =>
                build(persistedPhoneNumber, pn => {
                  val formattableNumber: FormattableNumber = numberFormatter.formatNumber(pn)
                  CallView(when, None, Some((pn, formattableNumber)))
                })
              case PersistedUndefinable(_) => None
            }
          }
          Ok(views.html.calls(name, calls))
        }
      case None => Future.successful(Forbidden(""))
    }
  }

  def subscribe = silhouette.SecuredAction(authorization).async(parse.tolerantJson) { implicit request =>
    request.body.validate[PushSubscription] match {
      case JsSuccess(pushSubscription, _) => browserPushService.subscribe(pushSubscription).map(_ => Created(""))
      case JsError(_) => Future.successful(BadRequest(Json.obj("error" -> "endpoint property is required")))
    }
  }

  def js = silhouette.SecuredAction(authorization) { implicit request =>
    Ok(views.js.index(browserPushService.publicKey()))
  }

  def serviceWorker = silhouette.SecuredAction(authorization).async { implicit request =>
    assets.at("/javascripts/service-worker.js")(request)
  }

  def updateContacts = silhouette.SecuredAction(authorization).async { implicit request =>
    val user: User = request.identity
    val upload: EitherT[Future, Seq[String], Int] = for {
      emailAddress <- EitherT(Future.successful(user.email.toRight(Seq("Email address required"))))
      accessToken <- EitherT(authInfoDao.find(user.loginInfo).map(_.toRight(Seq("Access token required"))))
      user <- EitherT.right(contactLoader.loadContacts(emailAddress, accessToken.accessToken))
      _ <- EitherT(contactService.upsertUser(user))
      persistedCallResponse <- EitherT(persistedCallDao.alterContacts(user.contacts))
    } yield {
      persistedCallResponse
    }
    upload.value.map {
      case Left(errs) => InternalServerError(errs.mkString("\n"))
      case Right(_) => NoContent
    }
  }

  def sendToModem = Action(parse.tolerantText(100)) { implicit request =>
    maybeModemSender match {
      case Some(modemSender) =>
        modemSender.send(request.body)
        NoContent
      case None =>
        NotFound
    }
  }

}

case class CallView(
                     when: Instant,
                     maybeContact: Option[Contact],
                     maybePhoneNumber: Option[(PhoneNumber, FormattableNumber)])
