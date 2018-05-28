package notify.sinks

import call._
import com.typesafe.scalalogging.StrictLogging
import number.NumberFormatter

class LoggingSink(numberFormatter: NumberFormatter) extends StrictLogging with (Call => Unit) {
  override def apply(call: Call): Unit = {
    val formattedCaller: String = call.caller match {
      case Withheld => "Withheld"
      case Known(name, _, number) => s"$name on ${numberFormatter.formatNumber(number).default}"
      case Unknown(number) => numberFormatter.formatNumber(number).default
      case Undefinable(str) => s"unparseable $str"
    }
    logger.info(s"Received call from $formattedCaller at ${call.when}")
  }
}
