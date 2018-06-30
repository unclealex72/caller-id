package contact

/**
  * The model for a user and their contact.s
  * @param emailAddress The user's email address.
  * @param contacts A list of the user's contacts.
  */
case class User(emailAddress: String, contacts: Seq[Contact])

/**
  * JSON codecs for [[User]]
  */
object User {
  import play.api.libs.json._

  implicit val userFormat: Format[User] = Json.format[User]
}
