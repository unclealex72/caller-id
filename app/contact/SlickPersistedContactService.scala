package contact

import slick.DatabaseProvider
import slick.dbio.{DBIOAction, Effect, NoStream, DBIO}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by alex on 07/09/15.
 */
class SlickPersistedContactService(databaseProvider: DatabaseProvider)(implicit ec: ExecutionContext) extends PersistedContactService {

  override def contactNamesAndPhoneTypesForNormalisedNumber(normalisedNumber: String): Future[Set[(ContactName, PhoneType)]] = {
    databaseProvider(phoneNumbers.findByNumber(normalisedNumber)).map(_.toSet)
  }

  override def userEmails: Future[Set[String]] = databaseProvider(users.emails).map(_.toSet)

  override def userExists(emailAddress: String): Future[Boolean] =
    databaseProvider(users.findByEmail(emailAddress)).map(_.isDefined)

  override def insertUser(emailAddress: String): Future[Boolean] = {
    userExists(emailAddress).flatMap { userExists =>
      if (userExists) Future(false) else databaseProvider(users.insertUser(emailAddress)).map(_ => true)
    }
  }

  def withUser[R, S <: NoStream, E <: Effect](emailAddress: String)(action: PersistedUser => Int => DBIOAction[R, S, E]): Future[Boolean] =
    databaseProvider(users.findByEmail(emailAddress)).flatMap { maybePersistedUser =>
      val maybeId = maybePersistedUser.flatMap(_.id)
      (maybePersistedUser, maybeId) match {
        case (Some(persistedUser), Some(id)) => databaseProvider(action(persistedUser)(id)).map(_ => true)
        case _ => Future(false)
      }
    }

  override def clearContacts(emailAddress: String): Future[Boolean] = withUser(emailAddress) { persistedUser => id =>
    DBIO.seq(phoneNumbers.deletePhoneNumbers(id), contacts.deleteContacts(id))
  }

  override def addContact(emailAddress: String, contactName: ContactName, phoneNumbers: Seq[Phone]): Future[Boolean] = {
    withUser(emailAddress) { persistedUser => id =>
      DBIO.seq(contacts.insertContact(persistedUser.id.get, contactName).flatMap { contactId =>
        insertPhoneNumbers(contactId, phoneNumbers)
      })
    }
  }

  def insertPhoneNumbers(contactId: Int, testPhoneNumbers: Seq[Phone]) = DBIO.seq(testPhoneNumbers.map { phoneNumber =>
    phoneNumbers.insertPhoneNumber(contactId, phoneNumber._1, phoneNumber._2)
  } :_*)

  override def allContacts: Future[Map[String, Map[ContactName, Seq[Phone]]]] = {
    databaseProvider(users.all).map(collateContacts)
  }

  def collateContacts(userContactPhones: Seq[(String, ContactName, PhoneNumber, PhoneType)]): Map[String, Map[ContactName, Seq[Phone]]] =
    userContactPhones.groupBy(_._1).mapValues(collatePhoneNumbers)

  def collatePhoneNumbers(userContactPhones: Seq[(String, ContactName, PhoneNumber, PhoneType)]) =
    userContactPhones.groupBy(_._2).mapValues(_.map(ucp => (ucp._3, ucp._4)))

}
