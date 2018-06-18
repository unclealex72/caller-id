package dialogflow

import cats.data.NonEmptyList

case class WebhookResponse(messages: NonEmptyList[String])

object WebhookResponse {

  import play.api.libs.json._

  implicit val webhookResponseWrites : OWrites[WebhookResponse] = (webhookResponse: WebhookResponse) => {
    val messages: NonEmptyList[String] = webhookResponse.messages
    val (init: List[String], last: String) = messages.tail.foldLeft((List.empty[String], messages.head)) { (acc, message) =>
      val (init, last) = acc
      (init :+ last, message)
    }
    val fulfillmentText: String = init match {
      case Nil => last
      case list => Seq(list.mkString(",\n"), last).mkString(" and\n")
    }
    Json.obj("fulfillmentText" -> JsString(fulfillmentText), "outputContexts" -> JsArray())
  }
}
