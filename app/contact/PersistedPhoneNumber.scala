package contact

import slick.lifted.{ForeignKeyQuery, Tag}
import slick.driver.PostgresDriver.api._

case class PersistedPhoneNumber(id: Option[Int], number: String, phoneType: Option[String], contactId: Int)

class PhoneNumbers(tag: Tag) extends Table[PersistedPhoneNumber](tag, "phonenumbers") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def number = column[String]("number")
  def phoneType = column[Option[String]]("type")
  def contactId = column[Int]("contact_id")
  def numberIndex = index("number_index", number, unique = false)
  def * = (id.?, number, phoneType, contactId) <> (PersistedPhoneNumber.tupled, PersistedPhoneNumber.unapply)

  // A reified foreign key relation that can be navigated to create a join
  def contact: ForeignKeyQuery[Contacts, PersistedContact] =
    foreignKey("CONTACT_FK", contactId, TableQuery[Contacts])(_.id, onDelete=ForeignKeyAction.Cascade)
}

object phoneNumbers extends TableQuery(new PhoneNumbers(_)) {

  val _schema = this.schema

  def findByNumber(number: String) = {
    val query = for {
      (p, c) <- phoneNumbers join contacts on (_.contactId === _.id) if p.number === number
    } yield (c.name, p.phoneType)
    query.result
  }

  def insertPhoneNumber(contactId: Int, phoneNumber: String, phoneNumberType: Option[String] = None) =
    phoneNumbers += PersistedPhoneNumber(None, phoneNumber, phoneNumberType, contactId)

  def deletePhoneNumbers(userId: Int) = {
    val phoneNumberIds = for {
      p <- phoneNumbers
      c <- p.contact
      u <- c.user if u.id === userId
    } yield p.id
    val deleteQuery = for {
      p <- phoneNumbers if p.id in phoneNumberIds
    } yield p
    deleteQuery.delete
  }
}