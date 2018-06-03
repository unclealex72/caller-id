package controllers

import java.time.Instant

import call.{Call => _, _}
import cats.data.Validated._
import cats.data._
import cats.implicits._
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
            override val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext)
  extends AbstractController(controllerComponents) {

  /*
  def index = Action { implicit request =>
    Redirect(routes.Home.calls()).withNewSession
  }
  */

  def index = calls

  def unauthorised = Action { implicit request =>
    Forbidden(views.html.goaway())
  }

  def calls = Action.async { implicit request =>
    val name: String = "Alex"
    def build(persistedPhoneNumber: PersistedPhoneNumber, builder: PhoneNumber => CallView): Option[CallView] = {
      numberLocationService.decompose(persistedPhoneNumber.normalisedNumber) match {
        case Valid(phoneNumber) => Some(builder(phoneNumber))
        case Invalid(_) => None
      }
    }
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
  }

  def updateContacts = Action.async { implicit request =>
    //val user: UserIdentity = request.user
    val emailAddress: String = ""//user.email
    val accessToken: String = ""//user.token.accessToken
    val upload: EitherT[Future, Seq[String], Unit] = for {
      user <- EitherT.right(contactLoader.loadContacts(emailAddress, accessToken))
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