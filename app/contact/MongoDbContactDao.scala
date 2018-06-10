package contact
import cats.data._
import cats.implicits._
import persistence.MongoDbDao
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

class MongoDbContactDao(reactiveMongoApi: ReactiveMongoApi) extends
  MongoDbDao(reactiveMongoApi, "contacts") with ContactDao {

  override def upsertUser(user: User)(implicit ec: ExecutionContext): Future[Either[Seq[String], Unit]] = {
    collection().flatMap { contacts =>
      val upsert = for {
        _ <- deleteUser(contacts, user.emailAddress)
        result <- insertUser(contacts, user)
      } yield {
        result
      }
      upsert.value
    }
  }

  def deleteUser(contacts: JSONCollection, emailAddress: String)(implicit ec: ExecutionContext): EitherT[Future, Seq[String], Unit] = {
    EitherT(contacts.remove("emailAddress" === emailAddress ).map(_.toEither))
  }

  def insertUser(contacts: JSONCollection, user: User)(implicit ec: ExecutionContext): EitherT[Future, Seq[String], Unit] = {
    val persistedContacts: Seq[PersistedContact] = user.contacts.map(contact =>
      PersistedContact(user.emailAddress, contact.normalisedPhoneNumber, contact.name, contact.phoneType, contact.avatarUrl))
    val insert: contacts.InsertBuilder[PersistedContact] = contacts.insert[PersistedContact](ordered = false)
    EitherT(insert.many(persistedContacts).map(_.toEither))
  }

  override def findContactNameAndPhoneTypeForPhoneNumber(normalisedPhoneNumber: String)(implicit ec: ExecutionContext): Future[Option[Contact]] = {
    for {
      contacts <- collection()
      cursor = contacts.find("normalisedPhoneNumber" === normalisedPhoneNumber).cursor[PersistedContact]()
      maybeContact <- cursor.headOption
    } yield {
      maybeContact.map { persistedContact =>
        Contact(normalisedPhoneNumber, persistedContact.name, persistedContact.phoneType, persistedContact.avatarUrl)
      }
    }
  }
}

case class PersistedContact(
                             userEmailAddress: String,
                             normalisedPhoneNumber: String,
                             name: String,
                             phoneType: PhoneType,
                             avatarUrl: Option[String])
object PersistedContact {
  implicit val persistedContactReads: Reads[PersistedContact] = Json.reads[PersistedContact]
  implicit val persistedContactWrites: OWrites[PersistedContact] = Json.writes[PersistedContact]
}
