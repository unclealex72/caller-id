package squeezebox


import akka.{Done, NotUsed}
import akka.stream._
import akka.stream.scaladsl.{Flow, Framing, Keep, Source}
import akka.util.ByteString
import com.google.api.client.util.escape.{Escaper, PercentEscaper}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success
import scala.util.matching.Regex

/**
  * The default implementation of [[Squeezebox]]
  * @param messageDuration The amount of time to show messages.
  * @param materializer The materializer used to materialize streams.
  * @param ec The execution context used to chain futures.
  */
class SqueezeboxImpl(messageDuration: FiniteDuration)(implicit materializer: Materializer, ec: ExecutionContext) extends Squeezebox with StrictLogging {

  val messageDurationInSeconds: Long = messageDuration.toSeconds

  override def display(serverFlow: Flow[ByteString, ByteString, _], message: String): Future[Done] =
    displayWithMaterializer(serverFlow, message)._2

  /**
    * Display a message on all squeezeboxes but tuple the server flow's materializer with a [[Done]] so that
    * the original materializer is not lost and the stream can be monitored for completion.
    * @param rawServerFlow The server flow for the media server.
    * @param message The message to send.
    * @tparam MAT The original materializer for the server flow.
    * @return
    */
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

    /**
      * Hold the state required to work out what the next request to the media server should be.
      * @param maybePlayerCount The number of players connected to the server, if currently known.
      * @param nextPlayerIndex The index of the next player to send a message to.
      * @param maybeNextRequest The next request that needs to be sent, if known.
      */
    case class State(maybePlayerCount: Option[Int], nextPlayerIndex: Int, maybeNextRequest: Option[SqueezeboxRequest]) {

      /**
        * Adjust the state with a known number of players.
        * @param count The number of players discovered.
        * @return A new state.
        */
      def withPlayerCount(count: Int): State =
        copy(maybePlayerCount = Some(count), maybeNextRequest = None)

      /**
        * Increment the next player index and clear the next request.
        * @return A new state.
        */
      def increment(): State = copy(nextPlayerIndex = nextPlayerIndex + 1, maybeNextRequest = None)

      /**
        * Set the next request.
        * @param nextRequest The next request to send.
        * @return A new state.
        */
      def withNextRequest(nextRequest: SqueezeboxRequest): State = copy(maybeNextRequest = Some(nextRequest))

      /**
        * Clear the next request.
        * @return A new state.
        */
      def continue(): State = copy(maybeNextRequest = None)
    }

    object State {
      /**
        * The initial state.
        * @return The initial state with no known players, a zero index and no known next request.
        */
      def zero: State = State(None, 0, None)
    }

    /**
      * A flow of squeezebox requests and responses. The basic flow is the following:
      * <ol>
      *   <li>Request the number of players connected to the server.</li>
      *   <li>Store this in the state.</li>
      *   <li>For each player:
      *     <ol>
      *       <li>Request the player's id.</li>
      *       <li>Send the message to that player.</li>
      *       <li>Increment the index in the state and either start again with the next player or exit.</li>
      *     </ol>
      *   </li>
      * </ol>
      */
    val notificationFlow: Flow[SqueezeboxResponse, SqueezeboxRequest, NotUsed] = Flow[SqueezeboxResponse].scan(State.zero) { (state, response) =>
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

  private val escape: String => String = {
    val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false)
    percentEscaper.escape
  }

  /**
    * The different requests that can be sent to a squeezebox.
    * @param request The request string to send to the squeezebox.
    */
  sealed abstract class SqueezeboxRequest(val request: String)

  /**
    * A request for the number of connected players.
    */
  object CountPlayers extends SqueezeboxRequest("player count ?")

  /**
    * A request for the id of a player.
    * @param index The index of the player who's id is required.
    */
  case class RequestId(index: Int) extends SqueezeboxRequest(s"player id $index ?")

  /**
    * A request to display a message on a player.
    * @param id The id of the player.
    * @param message The message to display.
    */
  case class DisplayMessage(id: String, message: String) extends SqueezeboxRequest(
    s"$id display ${escape(message)} ${escape(message)} $messageDurationInSeconds")

  /**
    * A request to exit from the media server.
    */
  object Exit extends SqueezeboxRequest("exit") {
    override def toString: String = "Exit()"
  }

  /**
    * The difference responses that can be returned from the media centre.
    */
  sealed trait SqueezeboxResponse

  /**
    * The number of players connected to the media centre.
    * @param count The number of players connected.
    */
  case class PlayerCount(count: Int) extends SqueezeboxResponse

  /**
    * The id of the player with the given index.
    * @param index The index of the player.
    * @param id The id of the player.
    */
  case class PlayerId(index: Int, id: String) extends SqueezeboxResponse

  /**
    * Acknowledge that a message is being displayed by a player.
    * @param id The id of the player displaying the message.
    * @param message The message being displayed.
    */
  case class DisplayingMessage(id: String, message: String) extends SqueezeboxResponse

  /**
    * An unknown response from the media centre.
    * @param text The full response.
    */
  case class Unknown(text: String) extends SqueezeboxResponse

  /**
    * Acknowledge that the media centre connection is to be closed.
    */
  object Exiting extends SqueezeboxResponse {
    override def toString: String = "Exiting()"
  }

  /**
    * A dummy message used to start the flow.
    */
  object Start extends SqueezeboxResponse {
    override def toString: String = "Start()"
  }

  object SqueezeboxResponse {

    private val playerCount: Regex = """player count ([0-9]+)""".r
    private val playerId: Regex = """player id ([0-9]+) ([0-9a-fA-F%]+)""".r
    private val display: Regex = """([0-9a-fA-F%]+) display (\S+) (\S+) ([0-9]+)""".r

    /**
      * Parse [[SqueezeboxResponse]]s from lines of text returned from the media centre.
      * @param response The line of text received.
      * @return The [[SqueezeboxResponse]] that encapsulates the response information.
      */
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
}
