package contact

import com.typesafe.scalalogging.StrictLogging
import number.NumberLocationService

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import number.{PhoneNumber => NPhoneNumber}


/**
 * Created by alex on 12/09/15.
 */
class ContactServiceImpl(val persistedContactService: PersistedContactService,
                                val numberLocationService: NumberLocationService)(implicit ec: ExecutionContext) extends ContactService
  with NonEmptyListFunctions with StrictLogging {


  override def contactNamesAndPhoneTypesForPhoneNumber(phoneNumber: NPhoneNumber): Future[Set[(ContactName, PhoneType)]] = {
    persistedContactService.contactNamesAndPhoneTypesForNormalisedNumber(phoneNumber.normalisedNumber)
  }

  override def clear(emailAddress: String): Future[Boolean] = {
    persistedContactService.clearContacts(emailAddress)
  }

  override def addContact(
                           emailAddress: String,
                           contactName: ContactName,
                           phoneNumbers: Seq[Phone]): Future[(Boolean, PhoneValidationResults)] = {
    val phoneNumberValidations = phoneNumbers.map { phoneNumber =>
      numberLocationService.decompose(phoneNumber._1).map {
        pn => (pn.normalisedNumber, phoneNumber._2)
      }
    }
    val validPhoneNumbers = phoneNumberValidations.flatMap(_.toOption)
    val errors = phoneNumberValidations.map(_.swap).flatMap(_.toOption).flatMap(_.stream)
    persistedContactService.addContact(emailAddress, contactName, validPhoneNumbers) map { result =>
      val phoneValidationResults = PhoneValidationResults(validPhoneNumbers, errors)
      if (result) {
        validPhoneNumbers.foreach { vpn =>
          logger info s"Persisted $vpn"
        }
        errors.foreach { error =>
          logger error error
        }
        true -> phoneValidationResults
      }
      else {
        false -> phoneValidationResults
      }
    }
  }

  override def insertOrUpdateUser(email: String): Future[Boolean] = {
    persistedContactService.insertUser(email)
  }
}
