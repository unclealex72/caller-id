package call

import java.time.{Instant, OffsetDateTime}

import cats.data.NonEmptyList
import contact.Contact
import persistence.MongoDbDaoSpec
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.{BSONArray, BSONDocument, BSONLong, BSONString}

class MongoDbPersistedCallDaoSpec extends MongoDbDaoSpec[MongoDbPersistedCallDao]("calls") {

  "Searching without time limits" should {
    "return all calls" in { f =>
      f.dao.calls(None, None, None).map { persistedCalls =>
        persistedCalls.withNames() should ===(Seq(fourthCall, thirdCall, secondCall, firstCall))
      }
    }
  }

  "Limiting the results" should {
    "return at most the desired number of calls" in { f =>
      f.dao.calls(max = Some(2)).map { persistedCalls =>
        persistedCalls.withNames() should ===(Seq(fourthCall, thirdCall))
      }
    }
  }

  "Searching with a lower time limit" should {
    "only return later calls" in { f =>
      f.dao.calls(since = Some(instantAt("2018-05-28T11:10:00+00:00"))).map { persistedCalls =>
        persistedCalls.withNames() should ===(Seq(fourthCall, thirdCall, secondCall))
      }
    }
  }

  "Searching with an upper time limit" should {
    "only return earlier calls" in { f =>
      f.dao.calls(until = Some(instantAt("2018-05-29T11:10:00+00:00"))).map { persistedCalls =>
        persistedCalls.withNames() should ===(Seq(secondCall, firstCall))
      }
    }
  }

  "Searching with lower and upper time limits" should {
    "only return calls between the two times" in { f =>
      f.dao.calls(
        None,
        since = Some(instantAt("2018-05-28T11:10:00+00:00")),
        until = Some(instantAt("2018-05-29T11:10:00+00:00"))).map { persistedCalls =>
        persistedCalls.withNames() should ===(Seq(secondCall))
      }
    }
  }

  "Post updating a contact" should {
    "update all previous phone calls for their number with their details" in { f =>
      val brian: Contact = Contact("+44181811811", "Brian", "home", Some("http://brian"))
      for {
        result <- f.dao.alterContacts(Seq(brian))
        calls <- f.dao.calls()
      } yield {
        calls.withNames() should ===(Seq(fourthCallBrian, thirdCall, secondCallBrian, firstCall))
        result should ===(Right(2))
      }
    }
  }
  "Adding a call" should {
    "add it and not affect any other calls" in { f =>
      for {
        _ <- f.dao.insert(fifthCall)
        calls <- f.dao.calls()
      } yield {
        calls.withNames() should ===(Seq(fifthCall, fourthCall, thirdCall, secondCall, firstCall))
      }
    }
  }
  val firstCall: PersistedCallWithName = "2018-05-28T11:09:28+00:00".from(PersistedWithheld).named("firstCall")

  val secondCall: PersistedCallWithName = "2018-05-28T11:15:14+00:00".from(
    PersistedUnknown(
      PersistedPhoneNumber(
        "+44181811811",
        "+44 (181) 811811",
        Some("London"),
        NonEmptyList.of("England", "UK")))).named("secondCall")

  val secondCallBrian: PersistedCallWithName = "2018-05-28T11:15:14+00:00".from(
    PersistedKnown(
      "Brian", "home", Some("http://brian"),
      PersistedPhoneNumber(
        "+44181811811",
        "+44 (181) 811811",
        Some("London"),
        NonEmptyList.of("England", "UK")))).named("secondCallBrian")

  val thirdCall: PersistedCallWithName = "2018-05-29T12:19:15+00:00".from(
    PersistedKnown(
      "Freddie", "mobile", None,
      PersistedPhoneNumber(
        "+44777811811",
        "+44 (777) 811811",
        None,
        NonEmptyList.of("England", "UK")))).named("thirdCall")

  val fourthCall: PersistedCallWithName = "2018-05-30T11:15:14+00:00".from(
    PersistedUnknown(
      PersistedPhoneNumber(
        "+44181811811",
        "+44 (181) 811811",
        Some("London"),
        NonEmptyList.of("England", "UK")))).named("fourthCall")

  val fourthCallBrian: PersistedCallWithName = "2018-05-30T11:15:14+00:00".from(
    PersistedKnown(
      "Brian", "home", Some("http://brian"),
      PersistedPhoneNumber(
        "+44181811811",
        "+44 (181) 811811",
        Some("London"),
        NonEmptyList.of("England", "UK")))).named("fourthCallBrian")

  val fifthCall: PersistedCallWithName = "2018-05-30T13:50:19+00:00".from(PersistedWithheld).named("fifthCall")

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
      ),
      BSONDocument(
        "when" -> BSONLong(1527678914000l),
        "caller" -> BSONDocument(
          "persistedPhoneNumber" -> BSONDocument(
            "normalisedNumber" -> BSONString("+44181811811"),
            "formattedNumber" -> BSONString("+44 (181) 811811"),
            "maybeCity" -> BSONString("London"),
            "countries" -> BSONArray(BSONString("England"), BSONString("UK"))
          ),
          "type" -> BSONString("unknown")
        )
      )
    )
  }

  override def createDao(reactiveMongoApi: ReactiveMongoApi): MongoDbPersistedCallDao = {
    new MongoDbPersistedCallDao(reactiveMongoApi)
  }

  case class PersistedCallWithName(persistedCall: PersistedCall, maybeName: Option[String]) {
    override def toString: String = maybeName.getOrElse(persistedCall.toString)
  }

  object PersistedCallWithName {

    private val knownCalls: Seq[PersistedCallWithName] =
      Seq(firstCall, secondCall, thirdCall, fourthCall, fifthCall, secondCallBrian, fourthCallBrian)
    def apply(persistedCall: PersistedCall): PersistedCallWithName = {
      knownCalls.find(_.persistedCall == persistedCall).getOrElse(PersistedCallWithName(persistedCall, None))
    }
  }

  implicit val persistedCallWithNameToPersistedCall: PersistedCallWithName => PersistedCall = _.persistedCall

  implicit class CreatePersistedCallImplicits(persistedCall: PersistedCall) {
    def named(name: String): PersistedCallWithName = PersistedCallWithName(persistedCall, Some(name))
  }

  implicit class CreatePersistedCallsImplicits(persistedCalls: Seq[PersistedCall]) {
    def withNames(): Seq[PersistedCallWithName] = persistedCalls.map(PersistedCallWithName.apply(_))
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
