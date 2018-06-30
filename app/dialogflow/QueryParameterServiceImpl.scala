package dialogflow

import java.time._

/**
  * The default implementation of [[QueryParameterService]]
  * @param clock The clock used to find out the current date and time.
  */
class QueryParameterServiceImpl(clock: Clock) extends QueryParameterService {

  override def createQueryParameters(webhookRequest: WebhookRequest): QueryParameters = {
    val (maybeCount: Option[Int], maybeFirst: Option[OffsetDateTime], maybeLast: Option[OffsetDateTime]) = webhookRequest match {
      case LastCall => (Some(1), None, None)
      case LastNumberOfCalls(count) => (Some(count), None, None)
      case CallsOnDay(date) => (None, Some(date), Some(date))
      case CallsDuringPeriod(start, finish) => (None, Some(start), Some(finish))
    }
    def withTime(maybeDate: Option[OffsetDateTime], localTime: LocalTime): Option[OffsetDateTime] = {
      maybeDate.map { date =>
        OffsetDateTime.of(date.toLocalDate, localTime, date.getOffset)
      }
    }
    QueryParameters(maybeCount, withTime(maybeFirst, LocalTime.MIN), withTime(maybeLast, LocalTime.MAX))
  }


}
