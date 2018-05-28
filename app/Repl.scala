import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, _}
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.util.ByteString

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

object Repl extends App {

  implicit val client = ActorSystem("SimpleTcpClient")
  implicit val materializer = ActorMaterializer()
  implicit val ec = client.dispatcher

  val address = "hurst"
  val port = 9090

  val connection: Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] = Tcp().outgoingConnection(address, port)

  val commandSource: Source[String, NotUsed] =
    Seq("players", "players 0").
      foldLeft(Source.empty[String])((acc, cmd) => acc.concat(Source.single(cmd))).
      throttle(2, 1.second, 2, ThrottleMode.Shaping)
  val offSwitch: Source[ByteString, Promise[Option[ByteString]]] = Source.maybe[ByteString]
  val input: Source[ByteString, Promise[Option[ByteString]]] =
    commandSource.map(cmd => ByteString(s"$cmd\n")).concatMat(offSwitch)(Keep.right)
  val flow: Source[String, Promise[Option[ByteString]]] =
    input.
    via(connection).
    via(Framing.delimiter(delimiter = ByteString('\n'), maximumFrameLength = Int.MaxValue, allowTruncation = true)).
    map(_.decodeString(StandardCharsets.UTF_8))

  flow.runWith(Sink.foreach(println)).onComplete(println)
}
