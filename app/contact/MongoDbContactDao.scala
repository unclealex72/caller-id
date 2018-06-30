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

/**
  * An implementation of [[ContactDao]] that uses MongoDB
  * @param reactiveMongoApi The underlying mongo API.
  * @param executionContext The execution context for futures.
  */
class MongoDbContactDao(reactiveMongoApi: ReactiveMongoApi)(implicit executionContext: ExecutionContext) extends
  MongoDbDao(reactiveMongoApi, "contacts") with ContactDao {

  override def upsertUser(user: User): Future[Either[Seq[String], Unit]] = {
    collection().flatMap { contacts =>
      val upsert: EitherT[Future, Seq[String], Unit] = for {
        _ <- deleteUser(contacts, user.emailAddress)
        result <- insertUser(contacts, user)
      } yield {
        result
      }
      upsert.value
    }
  }

  def deleteUser(contacts: JSONCollection, emailAddress: String): EitherT[Future, Seq[String], Unit] = {
    EitherT(contacts.remove("userEmailAddress" === emailAddress ).map(_.toEither))
  }

  def insertUser(contacts: JSONCollection, user: User): EitherT[Future, Seq[String], Unit] = {
    val persistedContacts: Seq[PersistedContact] = user.contacts.map(contact =>
      PersistedContact(user.emailAddress, contact.normalisedPhoneNumber, contact.name, contact.phoneType, contact.avatarUrl))
    val insert: contacts.InsertBuilder[PersistedContact] = contacts.insert[PersistedContact](ordered = false)
    EitherT(insert.many(persistedContacts).map(_.toEither))
  }

  override def findContactNameAndPhoneTypeForPhoneNumber(normalisedPhoneNumber: String): Future[Option[Contact]] = {
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

/**
  * The persisted, non-normalised, form of a contact.
  * @param userEmailAddress The email address of the user associated with the contact.
  * @param normalisedPhoneNumber The contact's normalised phone number.
  * @param name The name of the contact.
  * @param phoneType The phone type.
  * @param avatarUrl The contact's avatar URL, if they have one.
  */
case class PersistedContact(
                             userEmailAddress: String,
                             normalisedPhoneNumber: String,
                             name: String,
                             phoneType: String,
                             avatarUrl: Option[String])
object PersistedContact {
  implicit val persistedContactReads: Reads[PersistedContact] = Json.reads[PersistedContact]
  implicit val persistedContactWrites: OWrites[PersistedContact] = Json.writes[PersistedContact]
}
