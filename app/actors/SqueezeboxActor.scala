package actors

import akka.actor.Actor
import call.ReceivedCall
import com.typesafe.scalalogging.StrictLogging
import call.ReceivedCallFormatter
import call.ReceivedCallFormatter._
import squeezebox.Squeezebox

/**
 * Created by alex on 01/09/15.
 */
class SqueezeboxActor(val squeezebox: Squeezebox)(implicit val receivedCallFormatter: ReceivedCallFormatter) extends Actor with StrictLogging {

  override def receive = {
    case receivedCall: ReceivedCall => display(receivedCall)
  }

  def display(receivedCall: ReceivedCall) = {
    squeezebox.displayText(receivedCall.format)
  }
}
