package actors

import akka.actor.Actor
import call.{ReceivedCallFormatter, CallReceived}
import call.ReceivedCallFormatter._
import com.typesafe.scalalogging.StrictLogging

/**
 * Created by alex on 04/09/15.
 */
class CallLoggingActor(implicit val receivedCallFormatter: ReceivedCallFormatter) extends Actor with StrictLogging {

  def receive = {
    case callReceived: CallReceived =>
      logger.info(s"Received a call from ${callReceived.format}")
  }
}
