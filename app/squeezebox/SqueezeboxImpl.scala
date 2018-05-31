package squeezebox


import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl.{Flow, Framing, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import com.google.api.client.util.escape.{Escaper, PercentEscaper}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable._
import scala.concurrent.ExecutionContext

class SqueezeboxImpl(rawServerFlow: Flow[ByteString, ByteString, _], messageDuration: Int)(implicit materializer: Materializer, ec: ExecutionContext) extends Squeezebox with StrictLogging {
  override def display(message: String): Unit = {
    val fullServerFlow: Flow[SqueezeboxRequest, SqueezeboxResponse, NotUsed] =
    {
      val serverFlow: Flow[ByteString, SqueezeboxResponse, _] = rawServerFlow.map { raw =>
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
      Flow.fromFunction[SqueezeboxRequest, ByteString](request => ByteString(s"${request.request}\n")).via(serverFlow)
    }.merge(Source.single[SqueezeboxResponse](Start))

    val notificationFlow = Flow[SqueezeboxResponse].statefulMapConcat[SqueezeboxRequest](() => {
      var maybePlayerCount: Option[Int] = None
      var displayMessagesReceived = 0

      (response: SqueezeboxResponse) => {
        logger.debug(s"Received parsed response from server: $response")
        val nextRequests: Seq[SqueezeboxRequest] = response match {
          case Start =>
            Seq(CountPlayers)
          case PlayerCount(count) =>
            maybePlayerCount = Some(count)
            0.until(count).map(idx => RequestId(idx))
          case PlayerId(_, id) =>
            Seq(DisplayMessage(id, message))
          case DisplayingMessage(_, _) =>
            displayMessagesReceived += 1
            maybePlayerCount match {
              case Some(playerCount) if playerCount == displayMessagesReceived => Seq(Exit)
              case _ => Seq.empty
            }
          case Unknown(text) =>
            logger.warn(s"Received unknown response from server. Ignoring: $text")
            Seq.empty
        }
        logger.whenDebugEnabled {
          nextRequests.foreach { request =>
            logger.debug(s"Sending request: $request")
          }
        }
        nextRequests
      }})
    /*
    val notificationFlow = Flow.fromGraph(new NotificationFlow(text)).mapError {
      case e: Exception =>
        logger.error("Notification flow failed.", e)
        e
    }
    */
    fullServerFlow.join(notificationFlow).run()
  }

  class NotificationFlow(message: String) extends GraphStage[FlowShape[SqueezeboxResponse, SqueezeboxRequest]] {
    val in: Inlet[SqueezeboxResponse] = Inlet("NotificationFlowInlet")
    val out: Outlet[SqueezeboxRequest] = Outlet("NotificationFlowOutlet")

    override def shape: FlowShape[SqueezeboxResponse, SqueezeboxRequest] = FlowShape(in, out)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
      var maybePlayerCount: Option[Int] = None
      var displayMessagesReceived = 0

      setHandlers(in, out, new InHandler with OutHandler {
        override def onPush(): Unit = {
          val response = grab(in)
          try {
            logger.debug(s"Received parsed response from server: $response")
            val nextRequests: Seq[SqueezeboxRequest] = response match {
              case Start =>
                Seq(CountPlayers)
              case PlayerCount(count) =>
                maybePlayerCount = Some(count)
                0.until(count).map(idx => RequestId(idx))
              case PlayerId(_, id) =>
                Seq(DisplayMessage(id, message))
              case DisplayingMessage(_, _) =>
                displayMessagesReceived += 1
                maybePlayerCount match {
                  case Some(playerCount) if playerCount == displayMessagesReceived => Seq(Exit)
                  case _ => Seq.empty
                }
              case Unknown(text) =>
                logger.warn(s"Received unknown response from server. Ignoring: $text")
                Seq.empty
            }
            nextRequests.foreach(request => logger.debug(s"Sending request $request"))
            emitMultiple(out, nextRequests)
          } catch {
            case e: Exception =>
              logger.error("An unexpected error occurred whilst trying to respond to the media server.", e)
              fail(out, e)
          }
        }

        override def onPull(): Unit = {
          pull(in)
        }
      })
    }
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
    private val playerId = """player id ([0-9]+) ([0-9a-fA-F%3]+)""".r
    private val display = """([0-9a-fA-F%3]+) display (^\s+) (^\s+) ([0-9]+)""".r

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
