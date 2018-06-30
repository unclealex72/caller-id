package call

import java.time.{Instant, OffsetDateTime, ZoneId}

import contact.Contact
import number.PhoneNumber
import play.api.libs.json._

/**
  * The main model for received calls.
  * @param when The instant when the call was received.
  * @param caller The contact or number who made the call.
  */
case class Call(when: Long, caller: Caller) {

  /**
    * A more friendly view of this call
    */
  val view: CallView = {
    val (maybeContact, maybePhoneNumber): (Option[Contact], Option[PhoneNumber]) = caller match {
      case c @ Withheld => c.view
      case c: Known => c.view
      case c: Unknown => c.view
    }
    CallView(Instant.ofEpochMilli(when), maybeContact, maybePhoneNumber)
  }
}

/**
  * The model for who has made a call. This can be either [[Withheld]] if the number was withheld,
  * [[Known]] if the number is from a contact and [[Unknown]] if it is not.
  */
sealed trait Caller

/**
  * The caller instance for when the number is withheld.
  */
object Withheld extends Caller {

  private[call] val view: (Option[Contact], Option[PhoneNumber]) = (None, None)
}

/**
  * The caller instance for when a call is from a contact.
  * @param name The name of the contact.
  * @param phoneType The type of the phone.
  * @param avatarUrl A URL to an avatar for the contact, if any.
  * @param phoneNumber The number that called.
  */
case class Known(name: String, phoneType: String, avatarUrl: Option[String], phoneNumber: PhoneNumber)
  extends Caller {

  private[call] val view: (Option[Contact], Option[PhoneNumber]) =
    (Some(Contact(phoneNumber.normalisedNumber, name, phoneType, avatarUrl)), None)
}

/**
  * The caller instance for when a call is not from a contact.
  * @param phoneNumber The number that called.
  */
case class Unknown(phoneNumber: PhoneNumber) extends Caller {
  private[call] val view: (Option[Contact], Option[PhoneNumber]) = (None, Some(phoneNumber))
}

/**
  * A [[CallView]] is a view of a [[Call]] that is better for front ends. Both the contact and phone number are
  * optional and so this means that html templates, for example, do not have to do case matching.
  * @param when The instant the call was made.
  * @param contact The contact who called, if known.
  * @param phoneNumber The phone number that called, if known.
  */
case class CallView(
                     when: Instant,
                     contact: Option[Contact],
                     phoneNumber: Option[PhoneNumber]) {

  def whenWithTimezone(zoneId: ZoneId): OffsetDateTime = OffsetDateTime.ofInstant(when, zoneId)
}

/**
  * Json codecs for [[Call]]
  */
object Call {
  implicit val callFormat: OFormat[Call] = Json.format[Call]
}

/**
  * Json codecs for [[Caller]]. Basically, the subclass is determined by a discriminator property known as `type`
  */
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

  implicit val CallerFormat: OFormat[Caller] = {
    val writer: OWrites[Caller] = (o: Caller) => {
      val (obj, ty) = o match {
        case w@Withheld => (withheldFormat.writes(w), "withheld")
        case k: Known => (knownFormat.writes(k), "known")
        case u: Unknown => (unknownFormat.writes(u), "unknown")
      }
      obj + ("type" -> JsString(ty))
    }
    val reader: Reads[Caller] = {
      case jsobj: JsObject =>
        jsobj.value.get("type") match {
          case Some(JsString("withheld")) => withheldFormat.reads(jsobj)
          case Some(JsString("known")) => knownFormat.reads(jsobj)
          case Some(JsString("unknown")) => unknownFormat.reads(jsobj)
          case _ => JsError("caller.discriminator")
        }
      case _ => JsError("object.required")
    }
    OFormat(reader, writer)
  }
}

/**
  * Json codecs for [[CallView]]
  */
object CallView {
  implicit val callViewWrites: OWrites[CallView] = Json.writes[CallView]
}