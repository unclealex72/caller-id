package controllers

import java.time.Instant

import auth.DefaultEnv
import call.{Call => _, _}
import cats.data.Validated._
import cats.data._
import cats.implicits._
import com.mohiva.play.silhouette.api.{Authorization, Silhouette}
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.AuthInfoDAO
import contact.{Contact, ContactDao, ContactLoader}
import modem.ModemSender
import number.{NumberLocationService, PhoneNumber}
import play.api.mvc.{Filters => _, _}

import scala.concurrent.{ExecutionContext, Future}

class Home(
            val numberLocationService: NumberLocationService,
            val persistedCallDao: PersistedCallDao,
            val contactLoader: ContactLoader,
            val contactService: ContactDao,
            val maybeModemSender: Option[ModemSender],
            val silhouette: Silhouette[DefaultEnv],
            val authorization: Authorization[DefaultEnv#I, DefaultEnv#A],
            val authInfoDao: AuthInfoDAO[OAuth2Info],
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
                  CallView(when, Some(Contact(pn.normalisedNumber, contactName, phoneType, maybeAvatarUrl)), Some(pn)))
              case PersistedUnknown(persistedPhoneNumber) =>
                build(persistedPhoneNumber, pn => CallView(when, None, Some(pn)))
              case PersistedUndefinable(_) => None
            }
          }
          Ok(views.html.calls(name, calls))
        }
      case None => Future.successful(Forbidden(""))
    }
  }

  def updateContacts = silhouette.SecuredAction(authorization).async { implicit request =>
    val user = request.identity
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
      case Right(_) => Redirect(routes.Home.index())
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

case class CallView(when: Instant, maybeContact: Option[Contact], maybePhoneNumber: Option[PhoneNumber])
