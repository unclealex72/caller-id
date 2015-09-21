package contact

import scala.concurrent.Future
import scala.util.Try

/**
 * Created by alex on 07/09/15.
 */
trait PersistedContactService {

  def contactNamesAndPhoneTypesForNormalisedNumber(normalisedNumber: String): Future[Set[(ContactName, PhoneType)]]

  def userEmails: Future[Set[String]]

  def userExists(emailAddress: String): Future[Boolean]

  def insertUser(emailAddress: String): Future[Boolean]

  def clearContacts(emailAddress: String): Future[Boolean]

  def addContact(emailAddress: String, contactName: ContactName, phoneNumbers: Seq[Phone]): Future[Boolean]

  def allContacts: Future[Map[String, Map[ContactName, Seq[Phone]]]]
}
