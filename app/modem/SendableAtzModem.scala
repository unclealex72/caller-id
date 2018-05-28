package modem
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import akka.util.ByteString
import akka.{Done, NotUsed}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

class SendableAtzModem(implicit actorSystem: ActorSystem, materializer: Materializer)
  extends AtzModem with ModemSender with StrictLogging {

  val sink: Sink[ByteString, Future[Done]] = Sink.foreach[ByteString] { request =>
    logger.info(s"Received ${request.utf8String}")
  }
  val (queue, source) = Source.queue[ByteString](0, OverflowStrategy.backpressure).preMaterialize()
  val flow: Flow[ByteString, ByteString, NotUsed] = Flow.fromSinkAndSource(sink, source)

  override def createConnection(): Flow[ByteString, ByteString, _] = flow

  override def send(line: String): Unit = {
    val trimmedLine = line.trim
    logger.info(s"Sending to modem: $trimmedLine")
    queue.offer(ByteString(s"$trimmedLine\n"))
  }
}
