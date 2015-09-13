package contact

import slick.lifted.Tag
import slick.driver.PostgresDriver.api._

case class PersistedUser(id: Option[Int], email: String)

class Users(tag: Tag) extends Table[PersistedUser](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def uniqueEmailIndex = index("unique_email_index", email, unique = true)
  def * = (id.?, email) <> (PersistedUser.tupled, PersistedUser.unapply)
}

object users extends TableQuery(new Users(_)) {
  def findByEmail(email: String) = {
    val query = for {
      u <- users if u.email === email
    } yield u
    query.result.headOption
  }

  val _schema = this.schema

  def emails = {
    val query = for {
      u <- users
    } yield u.email
    query.result
  }

  //(ContactName, Seq[(PhoneNumber, PhoneType)])
  def all  = {
    val query = for {
      u <- users
      c <- contacts if c.userId === u.id
      p <- phoneNumbers if p.contactId === c.id
    } yield (u.email, c.name, p.number, p.phoneType)
    query.result
  }

  def insertUser(emailAddress: String) =
    (users returning users.map(_.id)) += PersistedUser(None, emailAddress)

}
