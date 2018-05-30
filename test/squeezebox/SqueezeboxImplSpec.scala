package squeezebox

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.scalatest.{AsyncWordSpec, Matchers}

class SqueezeboxImplSpec extends AsyncWordSpec with Matchers {

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
      s"$id1 display Hello Hello 30" -> s"$id1 display Hello Hello 30",
    )

    val toUtf8Flow: Flow[ByteString, String, NotUsed] = Flow.fromFunction { bytes =>
      bytes.utf8String.replaceAll("\n", "")
    }
    val logicFlow: Flow[String, String, NotUsed] = Flow.fromFunction { request =>
      serverResponses.getOrElse(request, request)
    }
    val fromUtf8Flow: Flow[String, ByteString, NotUsed] = Flow.fromFunction { str =>
      ByteString(s"$str\n")
    }
    toUtf8Flow.takeWhile(request => request != "exit").via(logicFlow).via(fromUtf8Flow)
  }

  "sending a request to display a message on all squeezeboxes" should {
    "elicit the correct responses from the media server" in {
      val squeezeboxImpl = new SqueezeboxImpl(serverFlow, 30)
      squeezeboxImpl.display("Hello").map { responses =>
        responses should be === Seq.empty[String]
      }
    }
  }
}
