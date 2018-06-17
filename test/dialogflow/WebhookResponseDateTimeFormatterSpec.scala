package dialogflow

import java.time.format.DateTimeFormatter

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class WebhookResponseDateTimeFormatterSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {

  val isoDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
  val webhookResponseDateTimeFormatter: DateTimeFormatter = WebhookResponseDateTimeFormatter()

  val examples = Table(
    "times",
    "2018-06-04T12:43:15+01:00" -> "Monday the 4th of June at 12 43 p m",
    "2018-06-03T09:05:00+01:00" -> "Sunday the 3rd of June at 9 05 a m",
    "2018-06-21T15:15:15+01:00" -> "Thursday the 21st of June at 3 15 p m",
    "2018-06-22T03:15:30+01:00" -> "Friday the 22nd of June at 3 15 a m"
  )

  property("Each date should be formatted correctly.") {
    forAll(examples) { case (dateTime, expectedResult) =>
       webhookResponseDateTimeFormatter.format(isoDateTimeFormatter.parse(dateTime)) should === (expectedResult)
    }
  }
}
