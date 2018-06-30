package controllers

import dialogflow.WebhookRequest._
import dialogflow.WebhookResponse._
import dialogflow.{DialogflowService, WebhookRequest}
import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

/**
  * The controller that is used by DialogFlow to allow calls to be queried by Google Home.
  * @param dialogflowService The [[DialogflowService]] that will do the heavy lifting.
  * @param bearerToken The bearer token required for authorisation.
  * @param controllerComponents
  * @param ec
  */
class DialogflowController(
                            val dialogflowService: DialogflowService,
                            val bearerToken: String,
                            override val controllerComponents: ControllerComponents)
                          (implicit val ec: ExecutionContext)
extends AbstractController(controllerComponents) {

  val authorizationHeader: String = "Authorization"
  val requiredAuthorizationValue: String = s"Bearer $bearerToken"

  /**
    * Parse and process requests from Dialogflow.
    * @return
    */
  def webhook: Action[WebhookRequest] = Action.async(parse.json[WebhookRequest]) { implicit request =>
    request.headers.toSimpleMap.get(authorizationHeader) match {
      case Some(headerValue) if headerValue == requiredAuthorizationValue =>
        val webhookRequest: WebhookRequest = request.body
        dialogflowService.response(webhookRequest).map(webhookResponse => Ok(Json.toJson(webhookResponse)))
      case None => Future.successful(Unauthorized(JsString("Authorisation is required for access to this resource.")))
      case _ => Future.successful(Forbidden(JsString("You do not have access to this resource.")))
    }
  }
}
