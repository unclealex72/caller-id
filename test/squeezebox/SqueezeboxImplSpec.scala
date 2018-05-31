package squeezebox

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.concurrent.Future

class SqueezeboxImplSpec extends AsyncWordSpec with Matchers with StrictLogging {

  implicit val actorSystem: ActorSystem = ActorSystem("notifierSpec", ConfigFactory.empty())
  implicit val materializer: Materializer = ActorMaterializer()

  val serverFlow: Flow[ByteString, ByteString, NotUsed] = {
    val id0 = "00%3A00"
    val id1 = "01%3A01"

    val serverResponses: Map[String, String] = Map(
      "player count ?" -> "player count 2",
      "player id 0 ?" -> s"player id 0 $id0",
      "player id 1 ?" -> s"player id 1 $id1",
      s"$id0 display Hello Hello 30" -> s"$id0 display Hello Hello 30",
      s"$id1 display Hello Hello 30" -> s"$id1 display Hello Hello 30"
    )

    val toUtf8Flow: Flow[ByteString, String, NotUsed] = Flow.fromFunction { bytes =>
      bytes.utf8String.replaceAll("\n", "")
    }
    val logicFlow: Flow[String, String, NotUsed] = Flow.fromFunction { request =>
      logger.debug(s"Media server received request: $request")
      serverResponses.get(request) match {
        case Some(response) =>
          logger.debug(s"Media server sending response: $response")
          response
        case None =>
          logger.warn(s"Media server did not understand request: $request")
          request
      }
    }
    val fromUtf8Flow: Flow[String, ByteString, NotUsed] = Flow.fromFunction { str =>
      ByteString(s"$str\n")
    }
    toUtf8Flow.via(logicFlow).via(fromUtf8Flow)
  }.merge(Source.fromFuture(Future { Thread.sleep(3600 * 1000); ByteString("")}))

  "sending a request to display a message on all squeezeboxes" should {
    "elicit the correct responses from the media server" in {
      val squeezeboxImpl = new SqueezeboxImpl(serverFlow, 30)
      logger.info("Starting test")
      squeezeboxImpl.display("Hello")
      1 should === (1)
    }
  }
}
