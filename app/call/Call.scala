package call

import java.time.Instant

import contact.PhoneType
import number.PhoneNumber

case class Call(when: Instant, caller: Caller)

sealed trait Caller

object Withheld extends Caller

case class Known(name: String, phoneType: PhoneType, maybeAvatarUrl: Option[String], phoneNumber: PhoneNumber) extends Caller

case class Unknown(phoneNumber: PhoneNumber) extends Caller

case class Undefinable(number: String) extends Caller

