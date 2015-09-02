package actors

import akka.actor.Actor
import com.typesafe.scalalogging.StrictLogging
import modem._
import squeezebox.Squeezebox

/**
 * Created by alex on 01/09/15.
 */
class SqueezeboxActor(squeezebox: Squeezebox) extends Actor with StrictLogging {

  override def receive = {
    case Witheld => display("Withheld")
    case Number(number) => display(number)
  }

  def display(message: String) = {
    squeezebox.displayText(message, message)
  }


}
