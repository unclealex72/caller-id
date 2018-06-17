package dialogflow

trait QueryParameterService {

  def createQueryParameters(webhookRequest: WebhookRequest): QueryParameters
}
