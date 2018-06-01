package squeezebox

import akka.Done
import akka.stream.scaladsl.Flow
import akka.util.ByteString

import scala.concurrent.Future

trait Squeezebox {

  def display(serverFlow: Flow[ByteString, ByteString, _], message: String): Future[Done]
}
