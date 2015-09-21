package controllers

import argonaut.Argonaut._
import argonaut._
import contact._
import play.api.mvc.{Action, Controller}
import controllers.ArgonautJson._
import scala.concurrent.ExecutionContext
import controllers.Codecs._

/**
 * Created by alex on 13/09/15.
 */
class ContactsController(contactService: ContactService)(implicit ec: ExecutionContext) extends Controller {

  def deleteContacts(emailAddress: String) = Action.async { implicit request =>
    contactService.clear(emailAddress).map { success =>
      if (success) NoContent else NotFound
    }
  }

  def addContact(emailAddress: String) = Action.async(parser[GoogleContact]) { implicit request =>
    val googleContact = request.body
    val phoneNumbers = googleContact.googlePhoneNumbers.map(_.toPhone)
    contactService.addContact(emailAddress, googleContact.name, phoneNumbers) map { case (success, phoneValidationResults) =>
      val numbersAndErrors = NumbersAndErrors(phoneValidationResults.phoneNumbers.map(_._1), phoneValidationResults.errors)
      if (success) {
        NotFound(numbersAndErrors)
      }
      else {
        Created(numbersAndErrors)
      }
    }
  }
}

case class GoogleContact(name: String, googlePhoneNumbers: Seq[GooglePhoneNumber]) {
  def toContact: (ContactName, Seq[Phone]) = (name, googlePhoneNumbers.map(_.toPhone))
}
case class GooglePhoneNumber(number: String, phoneType: String) {
  def toPhone: Phone = (number, Some(phoneType))
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