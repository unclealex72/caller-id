package squeezebox

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{BidiFlow, Flow, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.concurrent.{Future, Promise}
import scala.util.Success
import scala.concurrent.duration._

class SqueezeboxImplSpec extends AsyncWordSpec with Matchers with StrictLogging {

  implicit val actorSystem: ActorSystem = ActorSystem("notifierSpec", ConfigFactory.empty())
  implicit val materializer: Materializer = ActorMaterializer()

  "sending a request to display a message on all squeezeboxes" should {
    "elicit the correct responses from the media server" in {
      val squeezeboxImpl: SqueezeboxImpl = new SqueezeboxImpl(messageDuration = 30.seconds)
      logger.info("Starting test")
      val (eventualRequests, eventualCompleted) = squeezeboxImpl.displayWithMaterializer(generateFlow(), "Hello")
      for {
        _ <- eventualCompleted.logging("Client side has completed.")
        requests <- eventualRequests.logging("Server side has completed.")
      } yield {
        requests should contain theSameElementsInOrderAs Seq(
          "player count ?",
          "player id 0 ?",
          "00%3A00 display Hello Hello 30",
          "player id 1 ?",
          "01%3A01 display Hello Hello 30",
          "exit")

      }
    }
  }

  implicit class Logging[A](fa: Future[A]) {
    def logging(msg: String): Future[A] = {
      logger.info(msg)
      fa
    }
  }
  def generateFlow(): Flow[ByteString, ByteString, Future[Seq[String]]] = {
    val responsesPromise: Promise[Seq[String]] = Promise()
    val logic: GraphStage[FlowShape[String, String]] = new GraphStage[FlowShape[String, String]] {
      val in: Inlet[String] = Inlet("logic.in")
      val out: Outlet[String] = Outlet("logic.out")

      override def shape: FlowShape[String, String] = FlowShape(in, out)

      override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
        new GraphStageLogic(shape) {

          val id0 = "00%3A00"
          val id1 = "01%3A01"

          val serverResponses: Map[String, String] = Map(
            "player count ?" -> "player count 2",
            "player id 0 ?" -> s"player id 0 $id0",
            "player id 1 ?" -> s"player id 1 $id1",
            s"$id0 display Hello Hello 30" -> s"$id0 display Hello Hello 30",
            s"$id1 display Hello Hello 30" -> s"$id1 display Hello Hello 30"
          )

          var requests: Seq[String] = Seq.empty
          var maybeNextResponse: Option[String] = None

          setHandlers(in, out, new InHandler with OutHandler {
            override def onPush(): Unit = {
              val request: String = grab(in)
              logger.debug(s"Media server received request: $request")
              requests = requests :+ request
              if (request == "exit") {
                responsesPromise.complete(Success(requests))
                maybeNextResponse = Some(request)
              }
              else {
                val nextResponse = serverResponses.get(request) match {
                  case Some(response) =>
                    logger.debug(s"Media server sending response: $response")
                    response
                  case None =>
                    logger.warn(s"Media server did not understand request: $request")
                    request
                }
                if (isAvailable(out)) {
                  push(out, nextResponse)
                  maybeNextResponse = None
                  pull(in)
                }
                else {
                  maybeNextResponse = Some(nextResponse)
                }
              }
            }

            override def onPull(): Unit = {
              maybeNextResponse.foreach(push(out, _))
              if (!hasBeenPulled(in)) {
                pull(in)
              }
            }
          })
        }
      }
    }

    val codec: BidiFlow[ByteString, String, String, ByteString, NotUsed] = BidiFlow.fromFunctions(
      outbound = { bytes => bytes.utf8String.replaceAll("\n", "") },
      inbound = { str => ByteString(s"$str\n") }
    )
    codec.join(Flow.fromGraph(logic)).mapMaterializedValue(_ => responsesPromise.future)
  }
}
