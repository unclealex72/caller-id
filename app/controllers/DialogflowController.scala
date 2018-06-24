package controllers

import dialogflow.{DialogflowService, WebhookRequest}
import dialogflow.WebhookRequest._
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import dialogflow.WebhookResponse._

import scala.concurrent.{ExecutionContext, Future}

class DialogflowController(
                            val dialogflowService: DialogflowService,
                            val bearerToken: String,
                            override val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext)
extends AbstractController(controllerComponents) {

  val authorizationHeader: String = "Authorization"
  val requiredAuthorizationValue: String = s"Bearer $bearerToken"

  def webhook = Action.async(parse.json[WebhookRequest]) { implicit request =>
    request.headers.toSimpleMap.get(authorizationHeader) match {
      case Some(headerValue) if headerValue == requiredAuthorizationValue =>
        val webhookRequest: WebhookRequest = request.body
        dialogflowService.response(webhookRequest).map(webhookResponse => Ok(Json.toJson(webhookResponse)))
      case None => Future.successful(Unauthorized(JsString("Authorisation is required for access to this resource.")))
      case _ => Future.successful(Forbidden(JsString("You do not have access to this resource.")))
    }
  }
}
