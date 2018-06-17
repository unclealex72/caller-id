package dialogflow

import java.time.OffsetDateTime

import scala.collection.immutable
import scala.reflect.ClassTag

sealed trait Intent

object LastCall extends Intent

case class LastNumberOfCalls(count: Int) extends Intent

case class CallsOnDay(date: OffsetDateTime) extends Intent

case class CallsDuringPeriod(
                              startTime: OffsetDateTime,
                              endTime: OffsetDateTime) extends Intent

object Intent {

  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads._
  import play.api.libs.json._
  import enumeratum._

  private sealed trait IntentName extends EnumEntry {
    val displayName:String
  }
  private abstract class AbstractIntentName(override val displayName: String) extends IntentName

  private object IntentName extends Enum[IntentName] {

    val values: immutable.IndexedSeq[IntentName] = findValues
    val displayNames: Seq[String] = values.map(_.displayName)

    object LastCall extends AbstractIntentName("last call")
    object LastNumberOfCalls extends AbstractIntentName("last number of calls")
    object CallsDuring extends AbstractIntentName("calls during")

  }

  private implicit val intentNameReads: Reads[IntentName] = JsPath.read[String].flatMap { str =>
    IntentName.values.find(_.displayName == str) match {
      case Some(intentName) => pure(intentName)
      case _ => (json: JsValue) => JsError(s"$json must be one of ${IntentName.displayNames.mkString(", ")}")
    }
  }

  implicit val intentReads: Reads[Intent] = {
    val queryResultPath = JsPath \ "queryResult"
    val parametersPath = queryResultPath \ "parameters"
    val intentNamePath = queryResultPath \ "intent" \ "displayName"
    val numberPath = parametersPath \ "number"
    val datePath = parametersPath \ "date"
    val datePeriodPath = parametersPath \ "date-period"

    def forIntentName(intentName: IntentName)(reads: Reads[Intent]): Reads[Intent] = {
      intentNamePath.read[IntentName].filter(_ == intentName).flatMap { _ =>
        reads
      }
    }
    val lastCallReads: Reads[Intent] = forIntentName(IntentName.LastCall)(pure(LastCall))
    val lastNumberOfCallsRead: Reads[Intent] = forIntentName(IntentName.LastNumberOfCalls) {
      numberPath.read[Int](min(1)).map(LastNumberOfCalls.apply)
    }
    val callsDuringReads: Reads[Intent] = forIntentName(IntentName.CallsDuring) {
      val callsOnDayReads: Reads[Intent] = datePath.read[OffsetDateTime].map(CallsOnDay.apply)
      val callsDuringPeriodReads: Reads[Intent] =
        ((datePeriodPath \ "startDate").read[OffsetDateTime] and (datePeriodPath \ "endDate").read[OffsetDateTime])(
          CallsDuringPeriod.apply _)
      callsOnDayReads.orElse(callsDuringPeriodReads)
    }
    lastCallReads.orElse(lastNumberOfCallsRead).orElse(callsDuringReads)
  }
}