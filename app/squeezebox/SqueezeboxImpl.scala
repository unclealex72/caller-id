package squeezebox


import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl.{Flow, Framing, Keep, Source}
import akka.util.ByteString
import com.google.api.client.util.escape.{Escaper, PercentEscaper}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable._
import scala.concurrent.ExecutionContext

class SqueezeboxImpl[MAT](rawServerFlow: Flow[ByteString, ByteString, MAT], messageDuration: Int)(implicit materializer: Materializer, ec: ExecutionContext) extends Squeezebox[MAT] with StrictLogging {
  override def display(message: String): MAT = {
    val fullServerFlow: Flow[SqueezeboxRequest, SqueezeboxResponse, MAT] =
    {
      val serverFlow: Flow[ByteString, SqueezeboxResponse, MAT] = rawServerFlow.map { raw =>
        logger.debug(s"Received raw: ${raw.utf8String}")
        raw
      }.via(Framing.delimiter(delimiter = ByteString('\n'), maximumFrameLength = Int.MaxValue, allowTruncation = true)).
        map(bs => bs.filter(by => by >= 32 && by <= 127)).
        map(_.utf8String.trim).
        filterNot(_.isEmpty).
        map { rawResponse =>
          logger.debug(s"Received from media server: $rawResponse")
          SqueezeboxResponse.parse(rawResponse)
        }
      Flow.fromFunction[SqueezeboxRequest, ByteString](request => ByteString(s"${request.request}\n")).viaMat(serverFlow)(Keep.right)
    }.merge(Source.single[SqueezeboxResponse](Start))

    val notificationFlow = Flow[SqueezeboxResponse].statefulMapConcat[SqueezeboxRequest](() => {
      var maybePlayerCount: Option[Int] = None
      var nextPlayerIndex = 0

      def requestNextIdOrExit(): Option[SqueezeboxRequest] = {
        maybePlayerCount match {
          case Some(playerCount) if playerCount == nextPlayerIndex =>
            logger.debug("All players have displayed the message so sending exit.")
            Some(Exit)
          case _ =>
            logger.debug("Not all players have displayed the message so requesting next ID.")
            Some(RequestId(nextPlayerIndex))
        }
      }

      response: SqueezeboxResponse => {
        logger.debug(s"Received parsed response from server: $response")
        val nextRequests: Option[SqueezeboxRequest] = response match {
          case Start =>
            Some(CountPlayers)
          case PlayerCount(count) =>
            logger.debug(s"There are $count players")
            maybePlayerCount = Some(count)
            requestNextIdOrExit()
          case PlayerId(_, id) =>
            Some(DisplayMessage(id, message))
          case DisplayingMessage(id, msg) =>
            logger.debug(s"Play $id has displayed message: $msg")
            nextPlayerIndex += 1
            requestNextIdOrExit()
          case Unknown(text) =>
            logger.warn(s"Received unknown response from server. Ignoring: $text")
            None
        }
        logger.whenDebugEnabled {
          nextRequests.foreach { request =>
            logger.debug(s"Sending request: $request")
          }
        }
        nextRequests match {
          case Some(nextRequest) => Seq(nextRequest)
          case None => Seq.empty
        }
      }})
    fullServerFlow.join(notificationFlow).run()
  }

  val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false)

  sealed trait SqueezeboxResponse
  case class PlayerCount(count: Int) extends SqueezeboxResponse
  case class PlayerId(index: Int, id: String) extends SqueezeboxResponse
  case class DisplayingMessage(id: String, message: String) extends SqueezeboxResponse
  case class Unknown(text: String) extends SqueezeboxResponse
  object Start extends SqueezeboxResponse {
    override def toString: String = "Start()"
  }

  object SqueezeboxResponse {

    private val playerCount = """player count ([0-9]+)""".r
    private val playerId = """player id ([0-9]+) ([0-9a-fA-F%]+)""".r
    private val display = """([0-9a-fA-F%]+) display (\S+) (\S+) ([0-9]+)""".r

    def parse(response: String): SqueezeboxResponse = {
      response match {
        case playerCount(count) => PlayerCount(Integer.parseInt(count))
        case playerId(index, id) => PlayerId(Integer.parseInt(index), id)
        case display(id, message, _, _) => DisplayingMessage(id, message)
        case _ =>
          logger.warn(s"Received unknown squeezebox response: $response")
          Unknown(response)
      }
    }
  }
  sealed abstract class SqueezeboxRequest(val request: String)
  object CountPlayers extends SqueezeboxRequest("player count ?")
  case class RequestId(index: Int) extends SqueezeboxRequest(s"player id $index ?")
  case class DisplayMessage(id: String, message: String) extends SqueezeboxRequest(
    s"$id display ${percentEscaper.escape(message)} ${percentEscaper.escape(message)} $messageDuration")
  object Exit extends SqueezeboxRequest("exit") {
    override def toString: String = "Exit()"
  }
}
