package slick

import java.util.UUID

import contact._
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.driver.PostgresDriver.api._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by alex on 07/09/15.
 */
case class InMemoryDatabaseProvider(testUsers: TU*)(implicit ec: ExecutionContext) extends DatabaseProvider {

  val db = Database.forURL(s"jdbc:h2:mem:${UUID.randomUUID()};MODE=PostgreSQL;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
  val schema = users._schema ++ contacts._schema ++ phoneNumbers._schema
  Await.result(db.run(DBIO.seq(schema.create, insertUsers(testUsers))), 1.second)

  def insertUsers(testUsers: Seq[TU]) = DBIO.seq(testUsers.map { user =>
    users.insertUser(user.email).flatMap { userId => insertContacts(userId, user.contacts) }
  }: _*)

  def insertContacts(userId: Int, testContacts: Seq[TC]) = DBIO.seq(testContacts.map { contact =>
    contacts.insertContact(userId, contact.name).flatMap { contactId => insertPhoneNumbers(contactId, contact.phoneNumbers) }
  }: _*)

  def insertPhoneNumbers(contactId: Int, testPhoneNumbers: Seq[TP]) = DBIO.seq(testPhoneNumbers.map { phoneNumber =>
    phoneNumbers.insertPhoneNumber(contactId, phoneNumber.phoneNumber, phoneNumber.phoneNumberType)
  }: _*)

  override def apply[R, S <: NoStream, E <: Effect](action: DBIOAction[R, S, E]): Future[R] = db.run(action)
}

case class TU(email: String, contacts: TC*)
case class TC(name: String, phoneNumbers: TP*)
case class TP(phoneNumber: String, phoneNumberType: Option[String])
object TP {
  def apply(phoneNumber: String): TP = TP(phoneNumber, None)
  def apply(phoneNumber: String, phoneNumberType: String): TP = TP(phoneNumber, Some(phoneNumberType))
}