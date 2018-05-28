package controllers

import com.gu.googleauth._
import contact.{ContactLoader, ContactDao}
import play.api.libs.json._
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc.{Filters => _, _}
import contact.User._
import modem.ModemSender
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

class Home(
            val authAction: AuthAction[AnyContent],
            val authConfig: GoogleAuthConfig,
            val groupChecker: GoogleGroupChecker,
            val requiredGoogleGroups: Set[String],
            val wsClient: WSClient,
            val contactLoader: ContactLoader,
            val contactService: ContactDao,
            val maybeModemSender: Option[ModemSender],
            override val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext)
  extends AbstractController(controllerComponents) with Filters with LoginSupport {

  type GoogleAuthRequest[A] = AuthenticatedRequest[A, UserIdentity]

  def authzAction: ActionBuilder[GoogleAuthRequest, AnyContent] =
    authAction andThen requireGroup[GoogleAuthRequest](includedGroups = requiredGoogleGroups)

  def index = Action { implicit request => Ok(views.html.index()) }

  def updateContacts = authzAction.async { implicit request =>
    val user = request.user
    val emailAddress = user.email
    val accessToken = user.token.accessToken
    for {
      user <- contactLoader.loadContacts(emailAddress, accessToken)
      response <- contactService.upsertUser(user)
    } yield {
      response match {
        case Right(_) => Ok(Json.toJson(user))
        case Left(errors) => InternalServerError(JsArray(errors.map(JsString)))
      }
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

  override val failureRedirectTarget: Call = routes.Home.index()
  override val defaultRedirectTarget: Call = routes.Home.index() //routes.Application.authenticated()

}