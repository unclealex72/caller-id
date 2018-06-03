package controllers

import java.time.Instant

import call.{Call => _, _}
import cats.data.Validated._
import cats.data._
import cats.implicits._
import com.gu.googleauth._
import contact.{Contact, ContactDao, ContactLoader}
import modem.ModemSender
import number.{NumberLocationService, PhoneNumber}
import play.api.libs.ws.WSClient
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc.{Filters => _, _}

import scala.concurrent.{ExecutionContext, Future}

class Home(
            val authAction: AuthAction[AnyContent],
            val authConfig: GoogleAuthConfig,
            val groupChecker: GoogleGroupChecker,
            val requiredGoogleGroups: Set[String],
            val wsClient: WSClient,
            val numberLocationService: NumberLocationService,
            val persistedCallDao: PersistedCallDao,
            val contactLoader: ContactLoader,
            val contactService: ContactDao,
            val maybeModemSender: Option[ModemSender],
            override val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext)
  extends AbstractController(controllerComponents) with Filters with LoginSupport {

  type GoogleAuthRequest[A] = AuthenticatedRequest[A, UserIdentity]

  def authzAction: ActionBuilder[GoogleAuthRequest, AnyContent] =
    authAction andThen requireGroup[GoogleAuthRequest](includedGroups = requiredGoogleGroups)

  /*
  def index = Action { implicit request =>
    Redirect(routes.Home.calls()).withNewSession
  }
  */

  def index = Action { implicit request =>
    Ok(views.html.index()).withNewSession
  }

  def unauthorised = Action { implicit request =>
    Forbidden(views.html.goaway())
  }

  def calls = authzAction.async { implicit request =>
    val name: String = request.user.fullName
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

  def updateContacts = authzAction.async { implicit request =>
    val user: UserIdentity = request.user
    val emailAddress: String = user.email
    val accessToken: String = user.token.accessToken
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
  /*
 * Redirect to Google with a signed anti-forgery token in the OAuth 'state'
 */
  def loginAction = Action.async { implicit request =>
    startGoogleLogin()
  }

  /*
   * Looks up user's identity via Google and (optionally) enforces required Google groups at login time.
   *
   * To re-check Google group membership on every page request you can use the `requireGroup` filter
   * (see `Application.scala`).
   */
  def oauth2Callback = Action.async { implicit request =>
    //     processOauth2Callback()  // without Google group membership checks
    processOauth2Callback(requiredGoogleGroups, groupChecker)  // with optional Google group checks
  }

  def logout = Action { implicit request =>
    Redirect(routes.Home.index()).withNewSession
  }

  override val failureRedirectTarget: Call = routes.Home.unauthorised()
  override val defaultRedirectTarget: Call = routes.Home.calls() //routes.Application.authenticated()

}

case class CallView(when: Instant, maybeContact: Option[Contact], maybePhoneNumber: Option[PhoneNumber])