package notify.sinks

import call._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

class LoggingSink extends CallSink with StrictLogging {
  override def consume(call: Call): Future[_] = Future.successful {
    val formattedCaller: String = call.caller match {
      case Withheld => "Withheld"
      case Known(name, _, _, number) => s"$name on ${number.formattedNumber}"
      case Unknown(number) => number.formattedNumber
      case Undefinable(str) => s"unparseable $str"
    }
    logger.info(s"Received call from $formattedCaller at ${call.when}")
  }
}
