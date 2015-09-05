package actors

import akka.actor.Actor
import com.typesafe.scalalogging.StrictLogging
import modem._

/**
 * Created by alex on 01/09/15.
 */
class ModemLoggingActor extends Actor with StrictLogging {

  override def receive = {
    case Ok => logger info "Received OK from the modem."
    case Ring => logger info "Received RING from the modem."
    case Withheld => logger info "Received a withheld number from the modem."
    case Unknown(line) => logger warn s"Received an unknown response from the modem: $line"
    case Number(number) => logger info s"Received a call from $number"
  }
}