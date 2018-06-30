package controllers

import auth.{DefaultEnv, User}
import call.{Call => _, _}
import cats.data._
import cats.implicits._
import com.mohiva.play.silhouette.api.{Authorization, Silhouette}
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.AuthInfoDAO
import contact.{ContactDao, ContactLoader}
import modem.ModemSender
import number.{NumberFormatter, PhoneNumberFactory}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Filters => _, _}
import push.{BrowserPushService, PushSubscription}

import scala.concurrent.{ExecutionContext, Future}

/**
  * The main controller used for interacting with browsers.
  * @param numberLocationService
  * @param numberFormatter
  * @param callDao
  * @param contactLoader
  * @param contactService
  * @param browserPushService
  * @param silhouette
  * @param authorization
  * @param authInfoDao
  * @param assets
  * @param callsTemplate
  * @param controllerComponents
  * @param ec
  */
class HomeController(
            val numberLocationService: PhoneNumberFactory,
            val numberFormatter: NumberFormatter,
            val callDao: CallDao,
            val contactLoader: ContactLoader,
            val contactService: ContactDao,
            val browserPushService: BrowserPushService,
            val silhouette: Silhouette[DefaultEnv],
            val authorization: Authorization[DefaultEnv#I, DefaultEnv#A],
            val authInfoDao: AuthInfoDAO[OAuth2Info],
            val assets: Assets,
            val callsTemplate: views.html.calls,
            override val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext)
  extends AbstractController(controllerComponents) {

  def unauthorised = Action { implicit request =>
    Forbidden(views.html.goaway())
  }

  def index: Action[AnyContent] = silhouette.SecuredAction(authorization).async { implicit request =>
    request.identity.fullName match {
      case Some(name) =>
        callDao.calls(max = Some(20)).map { calls =>
          val callViews: Seq[CallView] = calls.map(_.view)
          Ok(callsTemplate(name, callViews))
        }
      case None => Future.successful(Forbidden(""))
    }
  }

  /**
    * Subscribe to a push service for notifications.
    * @return
    */
  def subscribe: Action[JsValue] = silhouette.SecuredAction(authorization).async(parse.tolerantJson) { implicit request =>
    request.body.validate[PushSubscription] match {
      case JsSuccess(pushSubscription, _) => {
        val pushSubscriptionWithUser: PushSubscription = pushSubscription.copy(user = request.identity.email)
        browserPushService.subscribe(pushSubscriptionWithUser).map(_ => Created(""))
      }
      case JsError(_) => Future.successful(BadRequest(Json.obj("error" -> "endpoint property is required")))
    }
  }

  /**
    * Serve javascript for the main page.
    * @return
    */
  def js: Action[AnyContent] = silhouette.SecuredAction(authorization) { implicit request =>
    Ok(views.js.index(browserPushService.publicKey()))
  }

  /**
    * Serve javascript for the service worker.
    * @return
    */
  def serviceWorker: Action[AnyContent] = silhouette.SecuredAction(authorization).async { implicit request =>
    assets.at("/javascripts/service-worker.js")(request)
  }

  /**
    * Update a user's contacts.
    * @return
    */
  def updateContacts: Action[AnyContent] = silhouette.SecuredAction(authorization).async { implicit request =>
    val user: User = request.identity
    val upload: EitherT[Future, Seq[String], Int] = for {
      emailAddress <- EitherT(Future.successful(user.email.toRight(Seq("Email address required"))))
      accessToken <- EitherT(authInfoDao.find(user.loginInfo).map(_.toRight(Seq("Access token required"))))
      user <- EitherT.right(contactLoader.loadContacts(emailAddress, accessToken.accessToken))
      _ <- EitherT(contactService.upsertUser(user))
      persistedCallResponse <- EitherT(callDao.alterContacts(user.contacts))
    } yield {
      persistedCallResponse
    }
    upload.value.map {
      case Left(errs) => InternalServerError(errs.mkString("\n"))
      case Right(_) => NoContent
    }
  }
}

