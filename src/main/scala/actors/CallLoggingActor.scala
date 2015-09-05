package actors

import akka.actor.Actor
import call.ReceivedCall
import com.typesafe.scalalogging.StrictLogging

/**
 * Created by alex on 04/09/15.
 */
class CallLoggingActor extends Actor with StrictLogging {

  def receive = {
    case receivedCall: ReceivedCall => {
      val number = receivedCall.phoneNumber match {
        case Some(phoneNumber) => phoneNumber.map(_.normalisedNumber).valueOr(identity)
        case None => "Withheld"
      }
      logger.info(s"Received a call from $number")
    }
  }
}
