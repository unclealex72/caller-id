package call

import number.NumberFormatter
import number.NumberFormatter._

import scalaz._

/**
 * Created by alex on 12/09/15.
 */
class ReceivedCallFormatterImpl(implicit val numberFormatter: NumberFormatter) extends ReceivedCallFormatter {

  override def format(receivedCall: ReceivedCall): String = {
    receivedCall.phoneNumberAndContacts match {
      case Some(\/-((phoneNumber, contacts))) =>
        if (contacts.isEmpty) {
          phoneNumber.format.mkString(" ")
        } else {
          contacts.map {
            case (name, Some(phoneType)) => s"$name ($phoneType)"
            case (name, None) => s"$name"
          }.mkString(", ")
        }
      case Some(-\/(number)) => number
      case None => "Withheld"
    }
  }
}
