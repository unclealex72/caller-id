package notify.sinks

import call.Call
import com.typesafe.scalalogging.StrictLogging

object LoggingSink extends StrictLogging with (Call => Unit) {
  override def apply(call: Call): Unit = {
    logger.info(s"Received call $call")
  }
}
