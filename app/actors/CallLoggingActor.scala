package actors

import akka.actor.Actor
import call.{ReceivedCallFormatter, ReceivedCall}
import call.ReceivedCallFormatter._
import com.typesafe.scalalogging.StrictLogging

/**
 * Created by alex on 04/09/15.
 */
class CallLoggingActor(implicit val receivedCallFormatter: ReceivedCallFormatter) extends Actor with StrictLogging {

  def receive = {
    case receivedCall: ReceivedCall =>
      logger.info(s"Received a call from ${receivedCall.format}")
  }
}
