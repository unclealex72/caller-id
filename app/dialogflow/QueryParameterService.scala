package dialogflow

/**
  * The service used to extract [[QueryParameters]] from a [[WebhookRequest]].
  */
trait QueryParameterService {

  /**
    * Extract [[QueryParameters]] from a request.
    * @param webhookRequest The request from Dialogflow.
    * @return The query parameters that can be used to generate the response.
    */
  def createQueryParameters(webhookRequest: WebhookRequest): QueryParameters
}
