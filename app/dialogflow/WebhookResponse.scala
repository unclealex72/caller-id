package dialogflow

import cats.data.NonEmptyList
import misc.NonEmptyListExtensions._

/**
  * A class used to generate JSON to send back to Dialogflow.
  * @param messages The messages to be spoken.
  */
case class WebhookResponse(messages: NonEmptyList[String])

/**
  * An [[play.api.libs.json.OWrites]] for [[WebhookResponse]]
  */
object WebhookResponse {

  import play.api.libs.json._

  implicit val webhookResponseWrites : OWrites[WebhookResponse] = (webhookResponse: WebhookResponse) => {
    val fulfillmentText: String = webhookResponse.messages.join(",\n", " and\n")
    Json.obj("fulfillmentText" -> JsString(fulfillmentText), "outputContexts" -> JsArray())
  }
}
