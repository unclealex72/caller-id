package actors

import akka.actor.Actor
import call.ReceivedCall
import com.typesafe.scalalogging.StrictLogging

/**
 * Created by alex on 04/09/15.
 */
class CallLoggingActor extends Actor with StrictLogging {

  def receive = {
    case receivedCall: ReceivedCall => logger.info(s"Received a call from $receivedCall")
  }
}
