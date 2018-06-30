package modem

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, _}
import akka.util.ByteString

import scala.collection.immutable.Seq
import scala.util.matching.Regex
import scala.concurrent.duration._

/**
 * An base implementation for AT modems. This class contains the logic on how to interpret a stream of
  * ATZ responses but not how to connect to the modem.
 */
abstract class AtzModem(implicit actorSystem: ActorSystem, materializer: Materializer) extends Modem {

  def responses(): Source[ModemResponse, Disconnect] = {
    val numberRegex: Regex = """NMBR = ([0-9]+)""".r

    val killSwitchSource: Source[ByteString, Disconnect] = Source.maybe[ByteString].mapMaterializedValue { promise =>
      new Disconnect {
        override def disconnect(): Unit = promise.success(None)
      }
    }
    killSwitchSource.
      merge(restartableConnection()).
      via(Framing.delimiter(delimiter = ByteString('\n'), maximumFrameLength = Int.MaxValue, allowTruncation = true)).
      map(bs => bs.filter(by => by >= 32 && by <= 127)).
      map(_.utf8String.trim).
      filterNot(_.isEmpty).
      map {
        case "OK" => Ok
        case "R" => Ring
        case "NMBR = P" => Withheld
        case numberRegex(digits) => Number(digits)
        case line =>
          val bytes: List[Byte] = line.getBytes.toList
          val decodedLine: String = bytes.filter(by => by >= 32 && by <= 127).map(_.toChar).mkString("")
          Unknown(decodedLine)
      }
  }

  /**
    * Create the connection to a modem.
    * @return
    */
  def createConnection(): Flow[ByteString, ByteString, _]

  /**
    * Wrap the flow created using [[createConnection()]] so that it restarts on failure.
    * @return A flow that restarts when upstream fails.
    */
  def restartableConnection(): Source[ByteString, _] = {
    RestartSource.withBackoff(
      minBackoff = 1.second,
      maxBackoff = 1.minute,
      randomFactor = 0.2,
      maxRestarts = -1) { () =>
      val initialCommandsSource: Source[ByteString, NotUsed] =
        Source(Seq("ATZ", "AT+VCID=1", "AT+FCLASS=8").map(str => ByteString(s"$str\r\n")))
      val connection: Flow[ByteString, ByteString, _] = createConnection()
      initialCommandsSource.concat(Source.maybe).via(connection)
    }
  }
}
