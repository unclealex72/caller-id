package contact

import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, Tag}

case class PersistedContact(id: Option[Int], name: String, userId: Int)

class Contacts(tag: Tag) extends Table[PersistedContact](tag, "contacts") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def userId = column[Int]("user_id")
  def * = (id.?, name, userId) <> (PersistedContact.tupled, PersistedContact.unapply)

  // A reified foreign key relation that can be navigated to create a join
  def user: ForeignKeyQuery[Users, PersistedUser] = foreignKey("USER_FK", userId, TableQuery[Users])(_.id, onDelete=ForeignKeyAction.Cascade)
}

object contacts extends TableQuery(new Contacts(_)) {

  val _schema = this.schema

  def deleteContacts(userId: Int) = this.filter(_.userId === userId).delete

  def insertContact(userId: Int, name: String) =
    (this returning contacts.map(_.id)) += PersistedContact(None, name, userId)
}