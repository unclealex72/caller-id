package dialogflow

import java.time.OffsetDateTime

import scala.collection.immutable

sealed trait WebhookRequest

object LastCall extends WebhookRequest

case class LastNumberOfCalls(count: Int) extends WebhookRequest

case class CallsOnDay(date: OffsetDateTime) extends WebhookRequest

case class CallsDuringPeriod(
                              startTime: OffsetDateTime,
                              endTime: OffsetDateTime) extends WebhookRequest

object WebhookRequest {

  import enumeratum._
  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads._
  import play.api.libs.json._

  private sealed trait Intent extends EnumEntry {
    val displayName:String
  }
  private abstract class AbstractIntentName(override val displayName: String) extends Intent

  private object Intent extends Enum[Intent] {

    val values: immutable.IndexedSeq[Intent] = findValues
    val displayNames: Seq[String] = values.map(_.displayName)

    object LastCall extends AbstractIntentName("last call")
    object LastNumberOfCalls extends AbstractIntentName("last number of calls")
    object CallsDuring extends AbstractIntentName("calls during")

  }

  private implicit val intentNameReads: Reads[Intent] = JsPath.read[String].flatMap { str =>
    Intent.values.find(_.displayName == str) match {
      case Some(intentName) => pure(intentName)
      case _ => (json: JsValue) => JsError(s"$json must be one of ${Intent.displayNames.mkString(", ")}")
    }
  }

  implicit val intentReads: Reads[WebhookRequest] = {
    val queryResultPath: JsPath = JsPath \ "queryResult"
    val parametersPath: JsPath = queryResultPath \ "parameters"
    val intentNamePath: JsPath = queryResultPath \ "intent" \ "displayName"
    val numberPath: JsPath = parametersPath \ "number"
    val datePath: JsPath = parametersPath \ "date"
    val datePeriodPath: JsPath = parametersPath \ "date-period"

    def forIntentName(intentName: Intent)(reads: Reads[WebhookRequest]): Reads[WebhookRequest] = {
      intentNamePath.read[Intent].filter(_ == intentName).flatMap { _ =>
        reads
      }
    }
    val lastCallReads: Reads[WebhookRequest] = forIntentName(Intent.LastCall)(pure(LastCall))
    val lastNumberOfCallsRead: Reads[WebhookRequest] = forIntentName(Intent.LastNumberOfCalls) {
      numberPath.read[Int](min(1)).map(LastNumberOfCalls.apply)
    }
    val callsDuringReads: Reads[WebhookRequest] = forIntentName(Intent.CallsDuring) {
      val callsOnDayReads: Reads[WebhookRequest] = datePath.read[OffsetDateTime].map(CallsOnDay.apply)
      val callsDuringPeriodReads: Reads[WebhookRequest] =
        ((datePeriodPath \ "startDate").read[OffsetDateTime] and (datePeriodPath \ "endDate").read[OffsetDateTime])(
          CallsDuringPeriod.apply _)
      callsOnDayReads.orElse(callsDuringPeriodReads)
    }
    lastCallReads.orElse(lastNumberOfCallsRead).orElse(callsDuringReads)
  }
}