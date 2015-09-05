package call

import contact.Contact
import number.PhoneNumber
import org.joda.time.DateTime

import scalaz.\/

/**
 * A class that encapsulates a received call.
 * @author alex
 *
 */
case class ReceivedCall(
  /**
   * The date and time the call was received.
   */
  dateReceived: DateTime,
  /**
   * The number that made the call or just the number received by the modem if the full phone number could not be generated
   * or none if the number was withheld.
   */
  phoneNumber: Option[\/[String, PhoneNumber]],
  /**
   * The name of the contact associated with the phone number.
   */
  contact: Option[Contact])