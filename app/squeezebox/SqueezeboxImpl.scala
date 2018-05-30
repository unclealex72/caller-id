package squeezebox


import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl.{Broadcast, Flow, Framing, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import com.google.api.client.util.escape.{Escaper, PercentEscaper}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable._
import scala.concurrent.{ExecutionContext, Future}

class SqueezeboxImpl(rawServerFlow: Flow[ByteString, ByteString, _], messageDuration: Int)(implicit materializer: Materializer, ec: ExecutionContext) extends Squeezebox with StrictLogging {
  override def display(text: String): Future[Seq[String]] = {
    val fullServerFlow: Flow[SqueezeboxRequest, SqueezeboxResponse, _] =
    {
      val serverFlow: Flow[ByteString, SqueezeboxResponse, _] = rawServerFlow.
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
      Flow.fromFunction[SqueezeboxRequest, ByteString](request => ByteString(s"${request.request}\n")).via(serverFlow)
    }

    val notificationFlow =
      Flow[SqueezeboxResponse].concat(Source.single(Start)).via(Flow.fromGraph(new NotificationFlow(text)))
    fullServerFlow.join(notificationFlow).run()
    val flow = Flow.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val in: Inlet[SqueezeboxRequest] = Inlet("squeezeboxRequest")

      val notification = builder.add(new NotificationFlow(text))
      val server = builder.add(fullServerFlow)
      server ~> notification
      notification ~> server
      FlowShape(server.in, notification.out)
    })
    Source.single(CountPlayers).via(flow).via(Flow.fromFunction(_.toString)).runFold(Seq.empty[String])(_ :+ _)
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
          val nextRequests: Seq[SqueezeboxRequest] = grab(in) match {
            case Start =>
              Seq(CountPlayers)
            case PlayerCount(count) =>
              maybePlayerCount = Some(count)
              0.until(count).map(idx => RequestId(idx))
            case PlayerId(_, id) => Seq(DisplayMessage(id, message))
            case DisplayingMessage(_, _) =>
              displayMessagesReceived += 1
              maybePlayerCount match {
                case Some(playerCount) if playerCount == displayMessagesReceived => Seq(Exit)
                case _ => Seq.empty
              }
          }
          if (nextRequests.isEmpty) {
            pull(in)
          }
          else {
            emitMultiple(out, nextRequests)
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
  object Start extends SqueezeboxResponse

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
