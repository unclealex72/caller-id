package dialogflow

import java.time.OffsetDateTime

sealed trait Intent

object LastCall extends Intent

case class LastNumberOfCalls(count: Int) extends Intent

case class CallsOnDay(date: OffsetDateTime) extends Intent

case class CallsDuringPeriod(
                              /**
                                * This part contains the *date*.
                                */
                              date: OffsetDateTime,
                              /**
                                * This part contains the start *time*. Combine with the date.
                                */
                              startTime: OffsetDateTime,
                              /**
                                * This part contains the end *time*. Combine with the date.
                                */
                              endTime: OffsetDateTime) extends Intent

object Intent {

  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._
  import json._

  implicit val intentReads: Reads[Intent] = {
    val queryResult: JsPath = JsPath \ "queryResult"
    val parameters: JsPath = queryResult \ "parameters"
    val intentName: JsPath = queryResult \ "intent" \ "displayName"
    def intentTypeReads(discriminator: String): Reads[String] = {
      val error: JsonValidationError = JsonValidationError("Cannot read an intent discriminator column")
      intentName.read[String](filter[String](error) { v: String => v == discriminator })
    }
    val lastCallReads: Reads[Intent] = intentTypeReads("last call").map(_ => LastCall)
    val lastNumbefOfCallsReads: Reads[Intent] = (
      intentTypeReads("last number of calls") and
        (parameters \ "number").read[Int]
    )((_, count) => LastNumberOfCalls(count))
    val callsDuringPeriodReads: Reads[Intent] = (
      intentTypeReads("calls during") and
        (parameters \ "date").read[OffsetDateTime] and
        (parameters \ "time-period" \ "startTime").read[OffsetDateTime] and
        (parameters \ "time-period" \ "endTime").read[OffsetDateTime]
    )((_, date, startTime, endTime) => CallsDuringPeriod(date, startTime, endTime))
    val callsOnDayReads: Reads[Intent] = (
      intentTypeReads("calls during") and
        (parameters \ "date").read[OffsetDateTime]
      )((_, date) => CallsOnDay(date))
    lastCallReads orElse lastNumbefOfCallsReads orElse callsDuringPeriodReads orElse callsOnDayReads
  }
}