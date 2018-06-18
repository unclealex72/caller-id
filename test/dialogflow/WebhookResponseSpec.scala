package dialogflow

import cats.data.NonEmptyList
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsValue, Json}

import scala.io.Source
import scala.util.Try

class WebhookResponseSpec extends WordSpec with Matchers with StrictLogging {

  "Serialising a webhook response for one call" should {
    "serialise with no joining characters" in {
      Right(Json.toJson(
        WebhookResponse(
          NonEmptyList.of(
            "First call")))) should === ("single-webhook-response.json".parsed())
    }
  }

  "Serialising a webhook response for two calls" should {
    "serialise and join the two with 'and'" in {
      Right(Json.toJson(
        WebhookResponse(
          NonEmptyList.of(
            "First call",
            "Second call")))) should === ("double-webhook-response.json".parsed())
    }
  }

  "Serialising a webhook response for three calls" should {
    "serialise and join the three with a comma and 'and'" in {
      Right(Json.toJson(
        WebhookResponse(
          NonEmptyList.of(
            "First call",
            "Second call",
            "Third call")))) should === ("triple-webhook-response.json".parsed())
    }
  }

  "Serialising a webhook response for four calls" should {
    "serialise and join the four with a comma and 'and'" in {
      Right(Json.toJson(
        WebhookResponse(
          NonEmptyList.of(
            "First call",
            "Second call",
            "Third call",
            "Fourth call")))) should === ("quadruple-webhook-response.json".parsed())
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
