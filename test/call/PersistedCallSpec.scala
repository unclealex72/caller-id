package call

import java.time.{Instant, OffsetDateTime}

import cats.data.NonEmptyList
import org.scalatest.{Matchers, WordSpec}
import call.PersistedCall._
import play.api.libs.json._

class PersistedCallSpec extends WordSpec with Matchers {

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

  def call(persistedCaller: PersistedCaller): PersistedCall = {
    val now: Instant = OffsetDateTime.parse("2018-05-28T11:09:28+00:00").toInstant
    PersistedCall(now, persistedCaller)
  }

  val phoneNumber: PersistedPhoneNumber =
    PersistedPhoneNumber("+44181811811", "+44 (181) 811811", Some("London"), NonEmptyList.of("England", "UK"))
  val withheld: PersistedCall = call(PersistedWithheld)
  val _withheld: String = """{
                            |  "when" : 1527505768000,
                            |  "caller" : {
                            |    "type" : "withheld"
                            |  }
                            |}""".stripMargin
  val known: PersistedCall = call(PersistedKnown("Freddie", "mobile", phoneNumber))
  val _known: String = """{
                         |  "when" : 1527505768000,
                         |  "caller" : {
                         |    "name" : "Freddie",
                         |    "phoneType" : "mobile",
                         |    "persistedPhoneNumber" : {
                         |      "normalisedNumber" : "+44181811811",
                         |      "formattedNumber" : "+44 (181) 811811",
                         |      "maybeCity" : "London",
                         |      "countries" : [ "England", "UK" ]
                         |    },
                         |    "type" : "known"
                         |  }
                         |}""".stripMargin
  val unknown: PersistedCall = call(PersistedUnknown(phoneNumber))
  val _unknown: String = """{
                           |  "when" : 1527505768000,
                           |  "caller" : {
                           |    "persistedPhoneNumber" : {
                           |      "normalisedNumber" : "+44181811811",
                           |      "formattedNumber" : "+44 (181) 811811",
                           |      "maybeCity" : "London",
                           |      "countries" : [ "England", "UK" ]
                           |    },
                           |    "type" : "unknown"
                           |  }
                           |}""".stripMargin
  val undefinable: PersistedCall = call(PersistedUndefinable("unknown"))
  val _undefinable: String = """{
                               |  "when" : 1527505768000,
                               |  "caller" : {
                               |    "number" : "unknown",
                               |    "type" : "undefinable"
                               |  }
                               |}""".stripMargin
  

  implicit class SerialiseImplicits(persistedCall: PersistedCall) {
    def serialise(implicit persistedCallWrites: Writes[PersistedCall]): String = {
      Json.prettyPrint(Json.toJson(persistedCall)(persistedCallWrites))
    }
  }

  implicit class DeserialiseImplicits(str: String) {
    def deserialise(implicit persistedCallReads: Reads[PersistedCall]): PersistedCall = {
      Json.parse(str).as[PersistedCall](persistedCallReads)
    }
  }
}
