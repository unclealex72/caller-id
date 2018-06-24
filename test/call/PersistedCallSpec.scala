package call

import java.time.{Instant, OffsetDateTime}

import call.Call._
import cats.data.NonEmptyList
import number.PhoneNumber
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json._

class CallSpec extends WordSpec with Matchers {

  "Serialising a withheld call" should {
    "serialise correctly" in {
      withheld.serialise should ===(_withheld)
    }
  }

  "Serialising a call from a known contact" should {
    "serialise correctly" in {
      known.serialise should ===(_known)
    }
  }

  "Serialising a call from an unknown number" should {
    "serialise correctly" in {
      unknown.serialise should ===(_unknown)
    }
  }

  "Serialising a call from an undefinable number" should {
    "serialise correctly" in {
      undefinable.serialise should ===(_undefinable)
    }
  }

  "Deserialising a withheld call" should {
    "deserialise correctly" in {
      _withheld.deserialise should ===(withheld)
    }
  }

  "Deserialising a call from a known contact" should {
    "deserialise correctly" in {
      _known.deserialise should ===(known)
    }
  }

  "Deserialising a call from an unknown number" should {
    "deserialise correctly" in {
      _unknown.deserialise should ===(unknown)
    }
  }

  "Deserialising a call from an undefinable number" should {
    "deserialise correctly" in {
      _undefinable.deserialise should ===(undefinable)
    }
  }

  def call(Caller: Caller): Call = {
    val now: Instant = OffsetDateTime.parse("2018-05-28T11:09:28+00:00").toInstant
    Call(now, Caller)
  }

  val phoneNumber: PhoneNumber =
    PhoneNumber("+44181811811", "+44 (181) 811811", Some("London"), NonEmptyList.of("England", "UK"))
  val withheld: Call = call(Withheld)
  val _withheld: String = """{
                            |  "when" : 1527505768000,
                            |  "caller" : {
                            |    "type" : "withheld"
                            |  }
                            |}""".stripMargin
  val known: Call = call(Known("Freddie", "mobile", Some("http://freddie"), phoneNumber))
  val _known: String = """{
                         |  "when" : 1527505768000,
                         |  "caller" : {
                         |    "name" : "Freddie",
                         |    "phoneType" : "mobile",
                         |    "avatarUrl" : "http://freddie",
                         |    "phoneNumber" : {
                         |      "normalisedNumber" : "+44181811811",
                         |      "formattedNumber" : "+44 (181) 811811",
                         |      "city" : "London",
                         |      "countries" : [ "England", "UK" ]
                         |    },
                         |    "type" : "known"
                         |  }
                         |}""".stripMargin
  val unknown: Call = call(Unknown(phoneNumber))
  val _unknown: String = """{
                           |  "when" : 1527505768000,
                           |  "caller" : {
                           |    "phoneNumber" : {
                           |      "normalisedNumber" : "+44181811811",
                           |      "formattedNumber" : "+44 (181) 811811",
                           |      "city" : "London",
                           |      "countries" : [ "England", "UK" ]
                           |    },
                           |    "type" : "unknown"
                           |  }
                           |}""".stripMargin
  val undefinable: Call = call(Undefinable("unknown"))
  val _undefinable: String = """{
                               |  "when" : 1527505768000,
                               |  "caller" : {
                               |    "number" : "unknown",
                               |    "type" : "undefinable"
                               |  }
                               |}""".stripMargin
  

  implicit class SerialiseImplicits(call: Call) {
    def serialise(implicit CallWrites: Writes[Call]): String = {
      Json.prettyPrint(Json.toJson(call)(CallWrites))
    }
  }

  implicit class DeserialiseImplicits(str: String) {
    def deserialise(implicit CallReads: Reads[Call]): Call = {
      Json.parse(str).as[Call](CallReads)
    }
  }
}
