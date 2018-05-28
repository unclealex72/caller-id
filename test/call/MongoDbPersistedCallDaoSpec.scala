package call

import java.time.{Instant, OffsetDateTime}

import cats.data.NonEmptyList
import persistence.MongoDbDaoSpec
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.{BSONArray, BSONDocument, BSONLong, BSONString}

class MongoDbPersistedCallDaoSpec extends MongoDbDaoSpec[MongoDbPersistedCallDao]("calls") {

  "Searching without time limits" should {
    "return all calls" in { f =>
      f.dao.calls(None, None, None).map { persistedCalls =>
        persistedCalls should ===(Seq(firstCall, secondCall, thirdCall))
      }
    }
  }

  "Limiting the results" should {
    "return at most the desired number of calls" in { f =>
      f.dao.calls(Some(2), None, None).map { persistedCalls =>
        persistedCalls should ===(Seq(firstCall, secondCall))
      }
    }
  }

  "Searching with a lower time limit" should {
    "only return later calls" in { f =>
      f.dao.calls(None, Some(instantAt("2018-05-28T11:10:00+00:00")), None).map { persistedCalls =>
        persistedCalls should ===(Seq(secondCall, thirdCall))
      }
    }
  }

  "Searching with an upper time limit" should {
    "only return earlier calls" in { f =>
      f.dao.calls(None, None, Some(instantAt("2018-05-29T11:10:00+00:00"))).map { persistedCalls =>
        persistedCalls should ===(Seq(firstCall, secondCall))
      }
    }
  }

  "Searching with lower and upper time limits" should {
    "only return calls between the two times" in { f =>
      f.dao.calls(
        None,
        Some(instantAt("2018-05-28T11:10:00+00:00")),
        Some(instantAt("2018-05-29T11:10:00+00:00"))).map { persistedCalls =>
        persistedCalls should ===(Seq(secondCall))
      }
    }
  }

  val firstCall: PersistedCall = "2018-05-28T11:09:28+00:00".from(PersistedWithheld)

  val secondCall: PersistedCall = "2018-05-28T11:15:14+00:00".from(
    PersistedUnknown(
      PersistedPhoneNumber(
        "+44181811811",
        "+44 (181) 811811",
        Some("London"),
        NonEmptyList.of("England", "UK"))))

  val thirdCall: PersistedCall = "2018-05-29T12:19:15+00:00".from(
    PersistedKnown(
      "Freddie", "mobile",
      PersistedPhoneNumber(
        "+44777811811",
        "+44 (777) 811811",
        None,
        NonEmptyList.of("England", "UK"))))


  override def initialData(): Seq[BSONDocument] = {
    Seq(
      BSONDocument(
        "when" -> BSONLong(1527505768000l),
        "caller" -> BSONDocument(
          "type" -> BSONString("withheld")
        )
      ),
      BSONDocument(
        "when" -> BSONLong(1527506114000l),
        "caller" -> BSONDocument(
          "persistedPhoneNumber" -> BSONDocument(
            "normalisedNumber" -> BSONString("+44181811811"),
            "formattedNumber" -> BSONString("+44 (181) 811811"),
            "maybeCity" -> BSONString("London"),
            "countries" -> BSONArray(BSONString("England"), BSONString("UK"))
          ),
          "type" -> BSONString("unknown")
        )
      ),
      BSONDocument(
        "when" -> BSONLong(1527596355000l),
        "caller" -> BSONDocument(
          "name" -> BSONString("Freddie"),
          "phoneType" -> BSONString("mobile"),
          "persistedPhoneNumber" -> BSONDocument(
            "normalisedNumber" -> BSONString("+44777811811"),
            "formattedNumber" -> BSONString("+44 (777) 811811"),
            "countries" -> BSONArray(BSONString("England"), BSONString("UK"))
          ),
          "type" -> BSONString("known")
        )
      )
    )
  }

  override def createDao(reactiveMongoApi: ReactiveMongoApi): MongoDbPersistedCallDao = {
    new MongoDbPersistedCallDao(reactiveMongoApi)
  }

  implicit class CreateCallImplicits(when: String) {
    def from(persistedCaller: PersistedCaller): PersistedCall = {
      PersistedCall(instantAt(when), persistedCaller)
    }
  }
  def instantAt(when: String): Instant = {
    OffsetDateTime.parse(when).toInstant
  }
}
