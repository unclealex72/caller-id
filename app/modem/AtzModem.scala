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
 * An implementation for AT modems.
 */
abstract class AtzModem(implicit actorSystem: ActorSystem, materializer: Materializer) extends Modem {

  def responses(): Source[ModemResponse, Disconnect] = {
    val numberRegex: Regex = """NMBR = ([0-9]+)""".r

    val initialCommandsSource: Source[ByteString, NotUsed] =
      Source(Seq("ATZ", "AT+VCID=1", "AT+FCLASS=8").map(str => ByteString(s"$str\r\n")))
    val killSwitchSource: Source[ByteString, Disconnect] = Source.maybe[ByteString].mapMaterializedValue { promise =>
      new Disconnect {
        override def disconnect(): Unit = promise.success(None)
      }
    }
    initialCommandsSource.concatMat(killSwitchSource)(Keep.right).
      via(restartableConnection()).
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

  def createConnection(): Flow[ByteString, ByteString, _]

  /**
    * Wrap the flow created using [[createConnection()]] so that it restarts on failure.
    * @return A flow that restarts when upstream fails.
    */
  def restartableConnection(): Flow[ByteString, ByteString, _] = {
    RestartFlow.onFailuresWithBackoff(
      minBackoff = 1.second,
      maxBackoff = 1.minute,
      randomFactor = 0.2,
      maxRestarts = -1)(() => createConnection())
  }
}
