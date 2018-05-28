package modem

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging

class TcpAtzModem(host: String, port: Int)(implicit actorSystem: ActorSystem, materializer: Materializer) extends AtzModem with StrictLogging {
  override def createConnection(): Flow[ByteString, ByteString, _] = {
   logger.info(s"Initialising the modem from host $host on port $port")
    Tcp().outgoingConnection(host, port)
  }
}
