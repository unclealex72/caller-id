package controllers

import argonaut.Argonaut._
import argonaut._
import com.typesafe.scalalogging.StrictLogging
import contact._
import play.api.mvc.{Action, Controller}
import controllers.ArgonautJson._
import scala.concurrent.ExecutionContext
import controllers.Codecs._

/**
 * Created by alex on 13/09/15.
 */
class ContactsController(contactService: ContactService)(implicit ec: ExecutionContext) extends Controller with StrictLogging {

  def deleteContacts(emailAddress: String) = Action.async { implicit request =>
    logger.info(s"Clearing the contacts for $emailAddress")
    contactService.clear(emailAddress).map { success =>
      if (success) NoContent else NotFound
    }
  }

  def addContact(emailAddress: String) = Action.async(parser[GoogleContact]) { implicit request =>
    val googleContact = request.body
    logger.info(s"Adding contact ${googleContact.name} for user $emailAddress")
    val phoneNumbers = googleContact.googlePhoneNumbers.map(_.toPhone)
    contactService.addContact(emailAddress, googleContact.name, phoneNumbers) map { case (success, phoneValidationResults) =>
      val numbersAndErrors = NumbersAndErrors(phoneValidationResults.phoneNumbers.map(_._1), phoneValidationResults.errors)
      if (success) {
        Created(numbersAndErrors)
      }
      else {
        NotFound(numbersAndErrors)
      }
    }
  }
}

case class GoogleContact(name: String, googlePhoneNumbers: Seq[GooglePhoneNumber]) {
  def toContact: (ContactName, Seq[Phone]) = (name, googlePhoneNumbers.map(_.toPhone))
}
case class GooglePhoneNumber(number: String, phoneType: Option[String]) {
  def toPhone: Phone = (number, phoneType)
}
case class NumbersAndErrors(numbers: Seq[String], errors: Seq[String])

object Codecs {

  implicit def NumbersAndErrorsCodec: CodecJson[NumbersAndErrors] =
    casecodec2(NumbersAndErrors.apply, NumbersAndErrors.unapply)("numbers", "errors")
  implicit def GoogleContactCodec: CodecJson[GoogleContact] =
    casecodec2(GoogleContact.apply, GoogleContact.unapply)("name", "phoneNumbers")
  implicit def GooglePhoneNumberCodec: CodecJson[GooglePhoneNumber] =
    casecodec2(GooglePhoneNumber.apply, GooglePhoneNumber.unapply)("number", "type")
}