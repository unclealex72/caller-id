package dialogflow

import cats.data.NonEmptyList
import misc.NonEmptyListExtensions._

case class WebhookResponse(messages: NonEmptyList[String])

object WebhookResponse {

  import play.api.libs.json._

  implicit val webhookResponseWrites : OWrites[WebhookResponse] = (webhookResponse: WebhookResponse) => {
    val fulfillmentText: String = webhookResponse.messages.join(",\n", " and\n")
    Json.obj("fulfillmentText" -> JsString(fulfillmentText), "outputContexts" -> JsArray())
  }
}
