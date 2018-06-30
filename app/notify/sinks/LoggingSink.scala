package notify.sinks

import call._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

object LoggingSink extends CallSink with StrictLogging {
  override def consume(call: Call): Future[_] = Future.successful {
    val formattedCaller: String = call.caller match {
      case Withheld => "Withheld"
      case Known(name, _, _, number) => s"$name on ${number.formattedNumber}"
      case Unknown(number) => number.formattedNumber
    }
    logger.info(s"Received call from $formattedCaller at ${call.when}")
  }
}
