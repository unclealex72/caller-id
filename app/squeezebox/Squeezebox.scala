package squeezebox

import akka.Done
import akka.stream.scaladsl.Flow
import akka.util.ByteString

import scala.concurrent.Future

/**
  * Display messages on all Squeezeboxes in a household.
  */
trait Squeezebox {

  /**
    * Display messages on all Squeezeboxes in a household.
    * @param serverFlow An Akka stream representing a TCP connection to the Media Server.
    * @param message The message to send.
    * @return
    */
  def display(serverFlow: Flow[ByteString, ByteString, _], message: String): Future[Done]
}
