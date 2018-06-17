package dialogflow

import cats.data.NonEmptyList
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsValue, Json}

import scala.io.Source
import scala.util.Try

class WebhookResponseSpec extends WordSpec with Matchers with StrictLogging {

  "Serialising a webhook response" should {
    "serialise correctly" in {
      Right(Json.toJson(WebhookResponse(NonEmptyList.of("Somebody just called.", "But it wasn't me.")))) should === ("webhook-response.json".parsed())
    }
  }

  implicit class StringImplicits(resourceName: String) {

    def parsed(): Either[String, JsValue] = {
      val resource = s"dialogflow/$resourceName"
      for {
        serialised <- Try(Source.fromResource(resource, classOf[WebhookResponseSpec].getClassLoader).mkString).toEither.swap.map(_ => s"Cannot open resource $resource").swap
      } yield {
        Json.parse(serialised)
      }
    }
  }
}
