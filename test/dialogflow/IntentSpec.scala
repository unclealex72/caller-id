package dialogflow

import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

import scala.io.Source
import scala.util.Try

class IntentSpec extends WordSpec with Matchers with StrictLogging {

  "Deserialising a last call request" should {
    "deserialise correctly" in {
      "last-call.json".deserialised() should === (Right(LastCall))
    }
  }

  "Deserialising a last 10 calls request" should {
    "deserialise correctly" in {
      "last-10-calls.json".deserialised() should === (Right(LastNumberOfCalls(10)))
    }
  }

  "Deserialising a request for who called on Monday" should {
    "deserialise correctly" in {
      "who-called-monday.json".deserialised() should === (Right(
        CallsOnDay(OffsetDateTime.parse("2018-06-11T12:00:00+01:00"))))
    }
  }

  "Deserialising a request for who called last week" should {
    "deserialise correctly" in {
      "who-called-last-week.json".deserialised() should === (Right(
        CallsDuringPeriod(
          OffsetDateTime.parse("2018-06-04T12:00:00+01:00"),
          OffsetDateTime.parse("2018-06-10T12:00:00+01:00")
        ))
      )
    }
  }

  "Deserialising a request for who called this week" should {
    "deserialise correctly" in {
      "who-called-this-week.json".deserialised() should === (Right(
        CallsDuringPeriod(
          OffsetDateTime.parse("2018-06-11T12:00:00+01:00"),
          OffsetDateTime.parse("2018-06-17T12:00:00+01:00")
        ))
      )
    }
  }
  implicit class StringImplicits(resourceName: String) {

    def deserialised(): Either[String, Intent] = {
      val resource = s"dialogflow/$resourceName"
      for {
        serialised <- Try(Source.fromResource(resource, classOf[IntentSpec].getClassLoader).mkString).toEither.swap.map(_ => s"Cannot open resource $resource").swap
        intent <- {
          val json: JsValue = Json.parse(serialised)
          Json.fromJson[Intent](json) match {
            case JsSuccess(intent, _) => Right(intent)
            case JsError(validationErrorsByPath) =>
              val messages: Seq[String] = validationErrorsByPath.flatMap {
                case (path, errors) => errors.map { error =>
                  s"$path: $error"
                }
              }
              Left(messages.mkString("\n"))
          }
        }
      } yield {
        intent
      }
    }
  }
}
