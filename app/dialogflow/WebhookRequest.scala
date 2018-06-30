package dialogflow

import java.time.OffsetDateTime

import scala.collection.immutable

/**
  * A JSON request from Dialogflow.
  */
sealed trait WebhookRequest

/**
  * The request for who was the last caller.
  */
object LastCall extends WebhookRequest

/**
  * The request for the last number of calls.
  * @param count The maximum number of calls to return.
  */
case class LastNumberOfCalls(count: Int) extends WebhookRequest

/**
  * The request for calls on a given day.
  * @param date A date and time that falls on the requested day.
  */
case class CallsOnDay(date: OffsetDateTime) extends WebhookRequest

/**
  * The request for calls between two instants.
  * @param startTime The start time.
  * @param endTime The end time.
  */
case class CallsDuringPeriod(
                              startTime: OffsetDateTime,
                              endTime: OffsetDateTime) extends WebhookRequest

/**
  * An object that translates the request JSON from Dialogflow to a [[WebhookRequest]].
  */
object WebhookRequest {

  import enumeratum._
  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads._
  import play.api.libs.json._

  /**
    * An enumeration for the different Dialogflow intents.
    */
  private sealed trait Intent extends EnumEntry {

    /**
      * The name used to identify the intent in the request.
      */
    val name: String
  }

  private sealed abstract class AbstractIntent(override val name: String) extends Intent

  private object Intent extends Enum[Intent] {

    val values: immutable.IndexedSeq[Intent] = findValues
    val displayNames: Seq[String] = values.map(_.name)

    /**
      * The last call intent.
      */
    object LastCall extends AbstractIntent("last call")

    /**
      * The last number of calls intent.
      */
    object LastNumberOfCalls extends AbstractIntent("last number of calls")

    /**
      * The calls during intent.
      */
    object CallsDuring extends AbstractIntent("calls during")

  }

  private implicit val intentNameReads: Reads[Intent] = JsPath.read[String].flatMap { str =>
    Intent.values.find(_.name == str) match {
      case Some(intentName) => pure(intentName)
      case _ => (json: JsValue) => JsError(s"$json must be one of ${Intent.displayNames.mkString(", ")}")
    }
  }

  /**
    * Read an intent from JSON.
    */
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