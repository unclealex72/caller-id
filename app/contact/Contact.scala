package contact

case class Contact(normalisedPhoneNumber: String, name: String, phoneType: PhoneType)

object Contact {
  import play.api.libs.json._

  implicit val contactReads: Reads[Contact] = Json.reads[Contact]
  implicit val contactWrites: Writes[Contact] = Json.writes[Contact]
}
