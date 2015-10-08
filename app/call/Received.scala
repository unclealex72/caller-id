package call

import contact.{ContactName, PhoneType, PersistedContact}
import number.PhoneNumber
import org.joda.time.DateTime

import scalaz.\/

trait Received

/**
 * A class that encapsulates a received call.
 * @author alex
 *
 */
case class CallReceived(
  /**
   * The date and time the call was received.
   */
  dateReceived: DateTime,
  /**
   * The number that made the call or just the number received by the modem if the full phone number could not be generated
   * or none if the number was withheld. Contact information is included if it can be found.
   */
  phoneNumberAndContacts: Option[\/[String, (PhoneNumber, Set[(ContactName, PhoneType)])]]) extends Received

case object RingReceived extends Received