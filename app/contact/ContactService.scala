package contact

import number.{PhoneNumber => NPhoneNumber}

import scala.concurrent.Future
import scalaz._

/**
 * Created by alex on 12/09/15.
 */
trait ContactService {

  def insertOrUpdateUser(email: String): Future[Boolean]

  def contactNamesAndPhoneTypesForPhoneNumber(phoneNumber: NPhoneNumber): Future[Set[(ContactName, PhoneType)]]

  def update(emailAddress: String, contacts: Map[ContactName, Seq[Phone]]): Future[ValidationNel[String, Unit]]

}
