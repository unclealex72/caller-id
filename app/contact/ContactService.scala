package contact

import number.{PhoneNumber => NPhoneNumber}

import scala.concurrent.Future
import scala.language.implicitConversions
import scalaz.\/

/**
 * Created by alex on 12/09/15.
 */
trait ContactService {

  def insertOrUpdateUser(email: String): Future[Boolean]

  def contactNamesAndPhoneTypesForPhoneNumber(phoneNumber: NPhoneNumber): Future[Set[(ContactName, PhoneType)]]

  def clear(emailAddress: String): Future[Boolean]

  def addContact(emailAddress: String, contactName: ContactName, phoneNumbers: Seq[Phone]): Future[(Boolean, PhoneValidationResults)]
}

final case class PhoneValidationResults(phoneNumbers: Seq[Phone], errors: Seq[String])
