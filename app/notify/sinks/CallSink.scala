package notify.sinks

import call.Call

import scala.concurrent.Future

trait CallSink {

  def consume(call: Call): Future[_]
}
