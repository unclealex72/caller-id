package notify.sinks

import call._
import com.typesafe.scalalogging.StrictLogging
import number.NumberFormatter

class LoggingSink extends StrictLogging with (Call => Unit) {
  override def apply(call: Call): Unit = {
    val formattedCaller: String = call.caller match {
      case Withheld => "Withheld"
      case Known(name, _, _, number) => s"$name on ${number.formattedNumber}"
      case Unknown(number) => number.formattedNumber
      case Undefinable(str) => s"unparseable $str"
    }
    logger.info(s"Received call from $formattedCaller at ${call.when}")
  }
}
