package dialogflow

import scala.concurrent.Future

/**
  * The entry point for all dialogflow interactions.
  */
trait DialogflowService {

  /**
    * Eventually convert a [[WebhookRequest]] into a [[WebhookResponse]].
    * @param webhookRequest The request to consume from Dialogflow.
    * @return The eventual response to serve back to Dialogflow.
    */
  def response(webhookRequest: WebhookRequest): Future[WebhookResponse]
}
