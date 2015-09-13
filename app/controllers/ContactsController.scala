package controllers

import play.api.mvc.{Action, Controller}
import argonaut._, Argonaut._
import contact._
import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import GoogleCodecs._

/**
 * Created by alex on 13/09/15.
 */
class ContactsController(contactService: ContactService)(implicit ec: ExecutionContext) extends Controller {

  def update = Action.async(parse.json) { implicit request =>
    Parse.decodeValidation[GoogleUser](request.body.toString()).toValidationNel match {
      case Success(googleUser) =>
        contactService.update(googleUser.email, googleUser.toContacts).map {
          case Success(result) => Created("")
          case Failure(errors) => Created(errors.stream.mkString("\n"))
        }
      case Failure(errors) => Future {
        BadRequest(errors.stream.mkString("\n"))
      }
    }
  }
}

case class GoogleUser(email: String, googleContacts : Seq[GoogleContact]) {
  def toContacts: Map[ContactName, Seq[Phone]] = googleContacts.map(_.toContact).toMap
}
case class GoogleContact(name: String, googlePhoneNumbers: Seq[GooglePhoneNumber]) {
  def toContact: (ContactName, Seq[Phone]) = (name, googlePhoneNumbers.map(_.toPhone))
}
case class GooglePhoneNumber(number: String, phoneType: String) {
  def toPhone: Phone = (number, Some(phoneType))
}

object GoogleCodecs {

  implicit def GoogleUserCodec: CodecJson[GoogleUser] =
    casecodec2(GoogleUser.apply, GoogleUser.unapply)("email", "contacts")
  implicit def GoogleContactCodec: CodecJson[GoogleContact] =
    casecodec2(GoogleContact.apply, GoogleContact.unapply)("name", "phoneNumbers")
  implicit def GooglePhoneNumberCodec: CodecJson[GooglePhoneNumber] =
    casecodec2(GooglePhoneNumber.apply, GooglePhoneNumber.unapply)("number", "type")
}