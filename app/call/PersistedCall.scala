package call

import java.time.Instant

import cats.data.NonEmptyList
import contact.PhoneType
import play.api.libs.json._
import json._

case class PersistedCall(when: Instant, caller: PersistedCaller)

object PersistedCall {

  implicit val persistedCallReads: Reads[PersistedCall] = Json.reads[PersistedCall]
  implicit val persistedCallWrites: OWrites[PersistedCall] = Json.writes[PersistedCall]
}

sealed trait PersistedCaller

object PersistedWithheld extends PersistedCaller

case class PersistedKnown(name: String, phoneType: PhoneType, persistedPhoneNumber: PersistedPhoneNumber) extends PersistedCaller

case class PersistedUnknown(persistedPhoneNumber: PersistedPhoneNumber) extends PersistedCaller

case class PersistedUndefinable(number: String) extends PersistedCaller

object PersistedCaller {

  private val withheldFormat: OFormat[PersistedCaller] = new OFormat[PersistedCaller] {
    override def writes(o: PersistedCaller): JsObject = JsObject.empty

    override def reads(json: JsValue): JsResult[PersistedCaller] = {
      json match {
        case JsObject(_) => JsSuccess(PersistedWithheld)
        case _ => JsError("object.required")
      }
    }
  }
  private val persistedKnownFormat: OFormat[PersistedKnown] = Json.format[PersistedKnown]
  private val persistedUnknownFormat: OFormat[PersistedUnknown] = Json.format[PersistedUnknown]
  private val persistedUndefinableFormat: OFormat[PersistedUndefinable] = Json.format[PersistedUndefinable]

  implicit val persistedCallerFormat: Format[PersistedCaller] = {
    val writer: OWrites[PersistedCaller] = (o: PersistedCaller) => {
      val (obj, ty) = o match {
        case w@PersistedWithheld => (withheldFormat.writes(w), "withheld")
        case k: PersistedKnown => (persistedKnownFormat.writes(k), "known")
        case u: PersistedUnknown => (persistedUnknownFormat.writes(u), "unknown")
        case u: PersistedUndefinable => (persistedUndefinableFormat.writes(u), "undefinable")
      }
      obj + ("type" -> JsString(ty))
    }
    val reader: Reads[PersistedCaller] = {
      case jsobj: JsObject =>
        jsobj.value.get("type") match {
          case Some(JsString("withheld")) => withheldFormat.reads(jsobj)
          case Some(JsString("known")) => persistedKnownFormat.reads(jsobj)
          case Some(JsString("unknown")) => persistedUnknownFormat.reads(jsobj)
          case Some(JsString("undefinable")) => persistedUndefinableFormat.reads(jsobj)
          case _ => JsError("persistedCaller.discriminator")
        }
      case _ => JsError("object.required")
    }
    Format(reader, writer)
  }
}

case class PersistedPhoneNumber(
                                 normalisedNumber: String,
                                 formattedNumber: String,
                                 maybeCity: Option[String],
                                 countries: NonEmptyList[String])

object PersistedPhoneNumber {
  implicit val persistedPhoneNumberFormat: Format[PersistedPhoneNumber] = Json.format[PersistedPhoneNumber]
}