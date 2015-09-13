package contact

import number.NumberLocationService

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import number.{PhoneNumber => NPhoneNumber}
/**
 * Created by alex on 12/09/15.
 */
class ContactServiceImpl(val persistedContactService: PersistedContactService,
                                val numberLocationService: NumberLocationService)(implicit ec: ExecutionContext) extends ContactService with NonEmptyListFunctions {

  override def contactNamesAndPhoneTypesForPhoneNumber(phoneNumber: NPhoneNumber): Future[Set[(ContactName, PhoneType)]] = {
    persistedContactService.contactNamesAndPhoneTypesForNormalisedNumber(phoneNumber.normalisedNumber)
  }

  override def update(emailAddress: String, contacts: Map[ContactName, Seq[Phone]]): Future[ValidationNel[String, Unit]] = {
    val phoneNumbersByContactName = contacts.map { contact =>
      val name = contact._1
      val phoneNumbers = contact._2
      val validatedPhoneNumbers = phoneNumbers.map { phoneNumber =>
        numberLocationService.decompose(phoneNumber._1).map {
          pn => (pn.normalisedNumber, phoneNumber._2)
        }
      }
      (name, validatedPhoneNumbers)
    }
    def validatedPhoneNumberFilter[T](f: ValidationNel[String, (String, Option[String])] => Option[T]) = {
      phoneNumbersByContactName.mapValues { values => values.flatMap(f(_)) }
    }
    val validPhoneNumbers = validatedPhoneNumberFilter(_.toOption)
    val invalidPhoneNumbers = validatedPhoneNumberFilter(_.swap.toOption.map(_.stream))
    val errorMessages: List[String] = invalidPhoneNumbers.values.flatten.flatten.toList
    val result: ValidationNel[String, Unit] = errorMessages match {
      case Nil => Success {}
      case e :: es => Failure(nel(e, es))
    }
    persistedContactService.updateTo(emailAddress, validPhoneNumbers).map { _ => result }
  }

}
