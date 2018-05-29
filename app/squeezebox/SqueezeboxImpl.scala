package squeezebox


import akka.stream.scaladsl.{Flow, Framing}
import akka.util.ByteString
import com.google.api.client.util.escape.{Escaper, PercentEscaper}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable._
import scala.concurrent.{ExecutionContext, Future}

class SqueezeboxImpl(serverFlowProvider: () => Flow[ByteString, ByteString, _], messageDuration: Int) extends Squeezebox with StrictLogging {
  override def display(text: String)(implicit ec: ExecutionContext): Future[_] = {
    val rawServerFlow: Flow[ByteString, SqueezeboxResponse, _] = serverFlowProvider().
      via(Framing.delimiter(delimiter = ByteString('\n'), maximumFrameLength = Int.MaxValue, allowTruncation = true)).
      map(bs => bs.filter(by => by >= 32 && by <= 127)).
      map(_.utf8String.trim).
      filterNot(_.isEmpty).
      mapConcat[SqueezeboxResponse] { response =>
        logger.info(s"Received $response")
        SqueezeboxResponse.unapply(response) match {
          case Some(cmd) => Seq(cmd)
          case None => Seq.empty
        }
      }
    val serverFlow: Flow[SqueezeboxRequest, SqueezeboxResponse, _] =
      Flow.fromFunction[SqueezeboxRequest, ByteString](request => ByteString(s"${request.request}\n")).via(rawServerFlow)

    val logic : Flow[SqueezeboxResponse, Seq[SqueezeboxRequest], _] =
      Flow.fromFunction { response =>
        response match {
          case PlayerCount(count) => 0.until(count).map(idx => RequestId(idx))
          case PlayerId(_, id) => Seq(DisplayMessage(id, text))
        }
      }
  }

  val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false)

  sealed trait SqueezeboxResponse
  case class PlayerCount(count: Int) extends SqueezeboxResponse
  case class PlayerId(index: Int, id: String) extends SqueezeboxResponse
  case class DisplayingMessage(id: String, message: String) extends SqueezeboxResponse

  object SqueezeboxResponse {

    private val playerCount = """player count ([0-9]+)""".r
    private val playerId = """player id ([0-9]+) ([0-9a-F%3]+)""".r
    private val display = """([0-9a-F%3]+) display (^\s+) (^\s+) ([0-9]+)""".r

    def unapply(response: String): Option[SqueezeboxResponse] = {
      response match {
        case playerCount(count) => Some(PlayerCount(Integer.parseInt(count)))
        case playerId(index, id) => Some(PlayerId(Integer.parseInt(index), id))
        case display(id, message, _, _) => Some(DisplayingMessage(id, message))
        case _ => None
      }
    }
  }
  sealed abstract class SqueezeboxRequest(val request: String)
  object CountPlayers extends SqueezeboxRequest("player count ?")
  case class RequestId(index: Int) extends SqueezeboxRequest(s"player id $index ?")
  case class DisplayMessage(id: String, message: String) extends SqueezeboxRequest(
    s"$id display ${percentEscaper.escape(message)} ${percentEscaper.escape(message)} $messageDuration")
  object Exit extends SqueezeboxRequest("exit")
}
