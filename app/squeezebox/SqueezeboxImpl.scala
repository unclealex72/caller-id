package squeezebox


import akka.Done
import akka.stream._
import akka.stream.scaladsl.{Flow, Framing, Keep, Source}
import akka.util.ByteString
import com.google.api.client.util.escape.{Escaper, PercentEscaper}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success

class SqueezeboxImpl(messageDuration: FiniteDuration)(implicit materializer: Materializer, ec: ExecutionContext) extends Squeezebox with StrictLogging {

  val messageDurationInSeconds: Long = messageDuration.toSeconds

  override def display(serverFlow: Flow[ByteString, ByteString, _], message: String): Future[Done] = displayWithMaterializer(serverFlow, message)._2

  def displayWithMaterializer[MAT](rawServerFlow: Flow[ByteString, ByteString, MAT], message: String): (MAT, Future[Done]) = {
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

    val completedPromise: Promise[Done] = Promise[Done]
    case class State(maybePlayerCount: Option[Int], nextPlayerIndex: Int, maybeNextRequest: Option[SqueezeboxRequest]) {
      def withPlayerCount(count: Int): State =
        copy(maybePlayerCount = Some(count), maybeNextRequest = None)
      def increment(): State = copy(nextPlayerIndex = nextPlayerIndex + 1, maybeNextRequest = None)
      def withNextRequest(nextRequest: SqueezeboxRequest): State = copy(maybeNextRequest = Some(nextRequest))
      def continue(): State = copy(maybeNextRequest = None)
    }
    object State {
      def zero: State = State(None, 0, None)
    }
    val notificationFlow = Flow[SqueezeboxResponse].scan(State.zero) { (state, response) =>
      def requestNextIdOrExit(state: State): State = {
        state.maybePlayerCount match {
          case Some(playerCount) if playerCount == state.nextPlayerIndex =>
            logger.debug("All players have displayed the message so sending exit.")
            state.withNextRequest(Exit)
          case _ =>
            logger.debug("Not all players have displayed the message so requesting next ID.")
            state.withNextRequest(RequestId(state.nextPlayerIndex))
        }
      }
      logger.debug(s"Received parsed response from server: $response")
      val nextState: State = response match {
        case Start =>
          state.withNextRequest(CountPlayers)
        case PlayerCount(count) =>
          logger.debug(s"There are $count players")
          requestNextIdOrExit(state.withPlayerCount(count))
        case PlayerId(_, id) =>
          state.withNextRequest(DisplayMessage(id, message))
        case DisplayingMessage(id, msg) =>
          logger.debug(s"Play $id has displayed message: $msg")
          requestNextIdOrExit(state.increment())
        case Unknown(text) =>
          logger.warn(s"Received unknown response from server. Ignoring: $text")
          state.continue()
        case Exiting =>
          logger.debug("Received exiting response from server")
          completedPromise.complete(Success(Done))
          state.continue()
      }
      logger.whenDebugEnabled {
        state.maybeNextRequest.foreach { request =>
          logger.debug(s"Sending request: $request")
        }
      }
      nextState
    }.mapConcat { state =>
      state.maybeNextRequest match {
        case Some(nextRequest) => Seq(nextRequest)
        case _ => Seq.empty
      }
    }
    fullServerFlow.mapMaterializedValue(m => m -> completedPromise.future).join(notificationFlow).run()
  }

  val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false)

  sealed trait SqueezeboxResponse
  case class PlayerCount(count: Int) extends SqueezeboxResponse
  case class PlayerId(index: Int, id: String) extends SqueezeboxResponse
  case class DisplayingMessage(id: String, message: String) extends SqueezeboxResponse
  case class Unknown(text: String) extends SqueezeboxResponse
  object Exiting extends SqueezeboxResponse {
    override def toString: String = "Exiting()"
  }
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
        case "exit" => Exiting
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
    s"$id display ${percentEscaper.escape(message)} ${percentEscaper.escape(message)} $messageDurationInSeconds")
  object Exit extends SqueezeboxRequest("exit") {
    override def toString: String = "Exit()"
  }
}
