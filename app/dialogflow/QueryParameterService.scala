package dialogflow

trait QueryParameterService {

  def createQueryParameters(intent: Intent): QueryParameters
}
