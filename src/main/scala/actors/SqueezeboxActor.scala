package actors

import akka.actor.Actor
import call.ReceivedCall
import com.typesafe.scalalogging.StrictLogging
import number.NumberFormatter
import number.NumberFormatter._
import squeezebox.Squeezebox

/**
 * Created by alex on 01/09/15.
 */
class SqueezeboxActor(val squeezebox: Squeezebox)(implicit val numberFormatter: NumberFormatter) extends Actor with StrictLogging {

  override def receive = {
    case receivedCall: ReceivedCall => display(receivedCall)
  }

  def display(receivedCall: ReceivedCall) = {
    val message = receivedCall.phoneNumber match {
      case Some(phoneNumber) =>
        phoneNumber.map(phoneNumber => (phoneNumber.format ++ phoneNumber.formatAddress).mkString(" ")).valueOr(identity)
      case None => "Withheld"
    }
    squeezebox.displayText(message)
  }


}
