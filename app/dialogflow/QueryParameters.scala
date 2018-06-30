package dialogflow

import java.time.OffsetDateTime

/**
  * A class to hold the parameters that a person may have asked for via Dialogflow.
  * @param max The maximum number of calls to return, if any.
  * @param since The earliest time for calls, if any.
  * @param until The latest time for calls, if any.
  */
case class QueryParameters(max: Option[Int], since: Option[OffsetDateTime], until: Option[OffsetDateTime])