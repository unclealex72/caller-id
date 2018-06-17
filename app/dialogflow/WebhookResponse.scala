package dialogflow

import cats.data.NonEmptyList

case class WebhookResponse(messages: NonEmptyList[String])

object WebhookResponse {

  import play.api.libs.json._

  implicit val webhookResponseWrites : OWrites[WebhookResponse] = (webhookResponse: WebhookResponse) => {
    val messages: NonEmptyList[JsObject] = webhookResponse.messages.map { message =>
      Json.obj("text" -> Json.obj("text" -> JsArray(Seq(JsString(message)))))
    }
    val fulfillmentMessages = JsArray(messages.toList)
    Json.obj("fulfillmentMessages" -> fulfillmentMessages, "outputContexts" -> JsArray())
  }
}
