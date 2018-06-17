package dialogflow

import java.time.OffsetDateTime

case class QueryParameters(max: Option[Int], since: Option[OffsetDateTime], until: Option[OffsetDateTime])