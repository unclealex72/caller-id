package contact

case class User(emailAddress: String, contacts: Seq[Contact])

object User {
  import play.api.libs.json._

  implicit val userReads: Reads[User] = Json.reads[User]
  implicit val userWrites: Writes[User] = Json.writes[User]
}
