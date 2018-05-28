package modem

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, _}
import akka.util.ByteString

import scala.collection.immutable.Seq
import scala.util.matching.Regex

/**
 * An implementation for AT modems.
 */
abstract class AtzModem(implicit actorSystem: ActorSystem, materializer: Materializer) extends Modem {

  def responses(): Source[ModemResponse, Disconnect] = {
    val numberRegex: Regex = """NMBR = ([0-9]+)""".r

    val initialCommandsSource: Source[ByteString, NotUsed] =
      Source(Seq("ATZ", "AT+FCLASS=1.0", "AT+VCID=1").map(ByteString(_)))
    val killSwitchSource: Source[ByteString, Disconnect] = Source.maybe[ByteString].mapMaterializedValue { promise =>
      new Disconnect {
        override def disconnect(): Unit = promise.success(None)
      }
    }
    initialCommandsSource.concatMat(killSwitchSource)(Keep.right).
      via(createConnection()).
      via(Framing.delimiter(delimiter = ByteString('\n'), maximumFrameLength = Int.MaxValue, allowTruncation = true)).
      map(bs => bs.filter(by => by >= 32 && by <= 127)).
      map(_.utf8String.trim).
      filterNot(_.isEmpty).
      map {
        case "OK" => Ok
        case "RING" => Ring
        case "NMBR = P" => Withheld
        case numberRegex(digits) => Number(digits)
        case line =>
          val bytes = line.getBytes.toList
          val decodedLine = bytes.filter(by => by >= 32 && by <= 127).map(_.toChar).mkString("")
          Unknown(decodedLine)
      }
  }

  def createConnection(): Flow[ByteString, ByteString, _]
}
