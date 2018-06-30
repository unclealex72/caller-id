package controllers

import auth.{DefaultEnv, UserService}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.UserAwareAction
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers.oauth2.GoogleProvider
import javax.inject.Inject
import play.api.Logger
import play.api.cache.AsyncCacheApi
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * The social auth controller.
 *
 * @param components The ControllerComponents.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info service implementation.
 */
class SocialAuthController @Inject()(
                                      components: ControllerComponents,
                                      env: Environment[DefaultEnv],
                                      userAwareAction: UserAwareAction,
                                      userService: UserService,
                                      authInfoRepository: AuthInfoRepository,
                                      googleProvider: GoogleProvider,
                                      cache: AsyncCacheApi)(implicit ec: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  def getAuthenticationPayload(maybeBody: Option[JsValue]): JsValue = {
    maybeBody match {
      case Some(body) =>
        body.\("oauthData").asOpt[JsObject] match {
          case Some(data) =>
            // this request is coming from a successful flow on ng2-ui-auth, let's take this part only
            data
          case None =>
            body
        }
      case _ =>
        JsNull
    }
  }

  /**
   * Authenticates a user against a social provider.
   *
   * @return The result to display.
   */
  def authenticate: Action[AnyContent] = Action.async { implicit request =>
      // build a new JSON body as our ng2-ui-auth client put the data somewhere specific
      val body = AnyContentAsJson(getAuthenticationPayload(request.body.asJson))
      googleProvider.authenticate()(request.withBody(body)).flatMap {
        case Left(result) =>
         Future.successful(result)
        case Right(authInfo) =>
          for {
            profile <- googleProvider.retrieveProfile(authInfo)
            user <- userService.save(profile)
            _ <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- env.authenticatorService.create(profile.loginInfo)
            token <- env.authenticatorService.init(authenticator)
            result <- env.authenticatorService.embed(token, Redirect(redirectOnLogin))
          } yield {
            env.eventBus.publish(LoginEvent(user, request))
            result
          }
      }.recover {
        case e: ProviderException =>
          Logger.error("Unexpected provider error", e)
          Unauthorized(Json.obj("message" -> Messages("could.not.authenticate")))
        case e: Exception =>
          Logger.error("Unexpected error", e)
          Unauthorized(Json.obj("message" -> Messages("could.not.authenticate")))
      }
  }

  /**
    * Manages the sign out action.
    */
  def signOut: Action[AnyContent] = userAwareAction(env).async { implicit request =>
    (request.identity, request.authenticator) match {
      case (Some(identity), Some(authenticator)) =>
        env.eventBus.publish(LogoutEvent(identity, request))
        env.authenticatorService.discard(authenticator, Ok(views.html.loggedout()))
      case _ =>
        Future.successful(Ok(views.html.loggedout()))
    }
  }

  def redirectOnLogin: Call = routes.HomeController.index()
}
