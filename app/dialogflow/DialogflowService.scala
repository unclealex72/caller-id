package dialogflow

import scala.concurrent.Future

trait DialogflowService {

  def response(webhookRequest: WebhookRequest): Future[WebhookResponse]
}
