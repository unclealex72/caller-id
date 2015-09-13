package contact

import slick.DatabaseProvider
import slick.dbio.DBIO

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

  override def updateTo(emailAddress: String, contacts: Map[ContactName, Seq[Phone]]): Future[Boolean] = {
    databaseProvider(users.findByEmail(emailAddress)).flatMap {
      case Some(persistedUser) => updateUser(persistedUser, contacts).map(_ => true)
      case None => Future(false)
    }
  }

  protected def updateUser(persistedUser: PersistedUser, contacts: Map[ContactName, Seq[Phone]]): Future[Any] = {
    persistedUser.id match {
      case Some(userId) =>
        databaseProvider(DBIO.sequence(Seq(removePhoneNumbers(userId), removeContacts(userId), insertContacts(persistedUser, contacts))))
      case _ => Future {}
    }
  }

  def removePhoneNumbers(userId: Int) = phoneNumbers.deletePhoneNumbers(userId)
  def removeContacts(userId: Int) = contacts.deleteContacts(userId)


  def insertContacts(persistedUser: PersistedUser, phoneNumbersByContact: Map[ContactName, Seq[Phone]]) =
    DBIO.seq(phoneNumbersByContact.toSeq.map { contactAndPhoneNumbers =>
      val (contact, phoneNumbers) = contactAndPhoneNumbers
      contacts.insertContact(persistedUser.id.get, contact).flatMap { contactId => insertPhoneNumbers(contactId, phoneNumbers)}
  } :_*)

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
