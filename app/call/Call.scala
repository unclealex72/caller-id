package call

import java.time.{Instant, OffsetDateTime, ZoneId}

import contact.{Contact, PhoneType}
import number.PhoneNumber
import play.api.libs.json._

case class Call(when: Instant, caller: Caller) {
  val view: Option[CallView] = caller.view(when)
}

sealed trait Caller {
  def view(when: Instant): Option[CallView]
}

object Withheld extends Caller {
  override def view(when: Instant): Option[CallView] = Some(CallView(when, None, None))
}

case class Known(name: String, phoneType: PhoneType, avatarUrl: Option[String], phoneNumber: PhoneNumber)
  extends Caller {
  override def view(when: Instant): Option[CallView] =
    Some(CallView(
      when,
      Some(Contact(phoneNumber.normalisedNumber, name, phoneType, avatarUrl)),
      None))
}

case class Unknown(phoneNumber: PhoneNumber) extends Caller {
  override def view(when: Instant): Option[CallView] = Some(CallView(when, None, Some(phoneNumber)))
}

case class Undefinable(number: String) extends Caller {
  override def view(when: Instant): Option[CallView] = None
}

case class CallView(
                     when: Instant,
                     contact: Option[Contact],
                     phoneNumber: Option[PhoneNumber]) {

  def whenWithTimezone(zoneId: ZoneId): OffsetDateTime = OffsetDateTime.ofInstant(when, zoneId)
}

object Call {
  implicit val callFormat: OFormat[Call] = Json.format[Call]
}

object Caller {

  private val withheldFormat: OFormat[Caller] = new OFormat[Caller] {
    override def writes(o: Caller): JsObject = JsObject.empty

    override def reads(json: JsValue): JsResult[Caller] = {
      json match {
        case JsObject(_) => JsSuccess(Withheld)
        case _ => JsError("object.required")
      }
    }
  }
  private val knownFormat: OFormat[Known] = Json.format[Known]
  private val unknownFormat: OFormat[Unknown] = Json.format[Unknown]
  private val undefinableFormat: OFormat[Undefinable] = Json.format[Undefinable]

  implicit val CallerFormat: OFormat[Caller] = {
    val writer: OWrites[Caller] = (o: Caller) => {
      val (obj, ty) = o match {
        case w@Withheld => (withheldFormat.writes(w), "withheld")
        case k: Known => (knownFormat.writes(k), "known")
        case u: Unknown => (unknownFormat.writes(u), "unknown")
        case u: Undefinable => (undefinableFormat.writes(u), "undefinable")
      }
      obj + ("type" -> JsString(ty))
    }
    val reader: Reads[Caller] = {
      case jsobj: JsObject =>
        jsobj.value.get("type") match {
          case Some(JsString("withheld")) => withheldFormat.reads(jsobj)
          case Some(JsString("known")) => knownFormat.reads(jsobj)
          case Some(JsString("unknown")) => unknownFormat.reads(jsobj)
          case Some(JsString("undefinable")) => undefinableFormat.reads(jsobj)
          case _ => JsError("caller.discriminator")
        }
      case _ => JsError("object.required")
    }
    OFormat(reader, writer)
  }
}

object CallView {
  implicit val callViewWrites: OWrites[CallView] = Json.writes[CallView]
}