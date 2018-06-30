package contact

/**
  * A contact is the model for a known person who has called. It is expected that the contact will have
  * come from Google and will be matched by their phone number.
  * @param normalisedPhoneNumber The contact's normalised phone number.
  * @param name The contact's name.
  * @param phoneType The type of phone (mobile, home, etc.)
  * @param avatarUrl The contact's avatar URL if they have one.
  */
case class Contact(
                    normalisedPhoneNumber: String,
                    name: String,
                    phoneType: String,
                    avatarUrl: Option[String])

/**
  * JSON codecs for [[Contact]].
  */
object Contact {
  import play.api.libs.json._

  implicit val contactFormat: Format[Contact] = Json.format[Contact]
}
