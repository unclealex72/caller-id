package actors

import akka.actor.Actor
import call.{RingReceived, CallReceived, ReceivedCallFormatter}
import com.typesafe.scalalogging.StrictLogging
import call.ReceivedCallFormatter._
import org.joda.time.DateTime
import squeezebox.Squeezebox

/**
 * Created by alex on 01/09/15.
 */
class SqueezeboxActor(val squeezebox: Squeezebox)(implicit val receivedCallFormatter: ReceivedCallFormatter) extends Actor with StrictLogging {

  var availableAt: DateTime = DateTime.now()

  override def receive = {
    case callReceived: CallReceived => displayText(callReceived.format, 30)
    case RingReceived => displayText("Phone ring", 1)
  }

  def displayText(text: String, duration: Int) = {
    val now = DateTime.now()
    if (!now.isBefore(availableAt)) {
      availableAt = now.plusSeconds(duration)
      squeezebox.displayText(text, duration)
    }
  }
}
