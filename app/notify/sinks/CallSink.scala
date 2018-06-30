package notify.sinks

import call.Call

import scala.concurrent.Future

/**
  * A convenience trait for sinks that take a [[Call]] and then do something with it.
  */
trait CallSink {

  /**
    * Do something with a [[Call]].
    * @param call The call that has just been received.
    * @return
    */
  def consume(call: Call): Future[_]
}
