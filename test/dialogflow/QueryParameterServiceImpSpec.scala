package dialogflow

import java.time.{Clock, OffsetDateTime}

import org.scalatest.{Matchers, WordSpec}

class QueryParameterServiceImpSpec extends WordSpec with Matchers {

  implicit val stringToOffsetDateTime: String => OffsetDateTime = OffsetDateTime.parse

  val now: OffsetDateTime = "2018-06-12T18:44:47+01:00"
  val clock: Clock = Clock.fixed(now.toInstant, now.getOffset)

  val queryParameterService = new QueryParameterServiceImpl(clock)
  def paramsFor(intent: Intent): QueryParameters = queryParameterService.createQueryParameters(intent)

  "Requesting the last call" should {
    "request one call" in {
      paramsFor(LastCall) should === (QueryParameters(Some(1), None, None))
    }
  }

  "Requesting the last six calls" should {
    "request six calls" in {
      paramsFor(LastNumberOfCalls(6)) should === (QueryParameters(Some(6), None, None))
    }
  }

  "Requesting the call for today" should {
    "request all the calls from midnight to midnight" in {
      paramsFor(CallsOnDay(now)) should === (
        QueryParameters(
          None,
          Some("2018-06-12T00:00:00+01:00"),
          Some("2018-06-12T23:59:59.999999999+01:00")))
    }
  }

  "Requesting the calls for last week" should {
    "request all the calls from midnight to midnight" in {
      paramsFor(CallsDuringPeriod("2018-06-04T12:00:00+01:00", "2018-06-10T12:00:00+01:00")) should === (
        QueryParameters(None, Some("2018-06-04T00:00:00+01:00"), Some("2018-06-10T23:59:59.999999999+01:00")))
    }
  }
}
