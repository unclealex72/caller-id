package call

import java.time.{Instant, OffsetDateTime}

import cats.data.NonEmptyList
import contact.Contact
import number.PhoneNumber
import persistence.MongoDbDaoSpec
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.{BSONArray, BSONDocument, BSONLong, BSONString}

class MongoDbCallDaoSpec extends MongoDbDaoSpec[MongoDbCallDao]("calls") {

  "Searching without time limits" should {
    "return all calls" in { f =>
      f.dao.calls(None, None, None).map { calls =>
        calls.withNames() should ===(Seq(fourthCall, thirdCall, secondCall, firstCall))
      }
    }
  }

  "Limiting the results" should {
    "return at most the desired number of calls" in { f =>
      f.dao.calls(max = Some(2)).map { calls =>
        calls.withNames() should ===(Seq(fourthCall, thirdCall))
      }
    }
  }

  "Searching with a lower time limit" should {
    "only return later calls" in { f =>
      f.dao.calls(since = Some(instantAt("2018-05-28T11:10:00+00:00"))).map { calls =>
        calls.withNames() should ===(Seq(fourthCall, thirdCall, secondCall))
      }
    }
  }

  "Searching with an upper time limit" should {
    "only return earlier calls" in { f =>
      f.dao.calls(until = Some(instantAt("2018-05-29T11:10:00+00:00"))).map { calls =>
        calls.withNames() should ===(Seq(secondCall, firstCall))
      }
    }
  }

  "Searching with lower and upper time limits" should {
    "only return calls between the two times" in { f =>
      f.dao.calls(
        None,
        since = Some(instantAt("2018-05-28T11:10:00+00:00")),
        until = Some(instantAt("2018-05-29T11:10:00+00:00"))).map { calls =>
        calls.withNames() should ===(Seq(secondCall))
      }
    }
  }

  "Post updating a contact" should {
    "update all previous phone calls for their number with their details" in { f =>
      val brian: Contact = Contact("+44181811811", "Brian", "homeController", Some("http://brian"))
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
  val firstCall: CallWithName = "2018-05-28T11:09:28+00:00".from(Withheld).named("firstCall")

  val secondCall: CallWithName = "2018-05-28T11:15:14+00:00".from(
    Unknown(
      PhoneNumber("+44181811811", "+44 (181) 811811", Some("London"), NonEmptyList.of("England", "UK")))).named("secondCall")

  val secondCallBrian: CallWithName = "2018-05-28T11:15:14+00:00".from(
    Known(
      "Brian", "homeController", Some("http://brian"),
      PhoneNumber("+44181811811", "+44 (181) 811811", Some("London"), NonEmptyList.of("England", "UK")))).named("secondCallBrian")

  val thirdCall: CallWithName = "2018-05-29T12:19:15+00:00".from(
    Known(
      "Freddie", "mobile", None,
      PhoneNumber("+44777811811", "+44 (777) 811811", None, NonEmptyList.of("England", "UK")))).named("thirdCall")

  val fourthCall: CallWithName = "2018-05-30T11:15:14+00:00".from(
    Unknown(
      PhoneNumber("+44181811811", "+44 (181) 811811", Some("London"), NonEmptyList.of("England", "UK")))).named("fourthCall")

  val fourthCallBrian: CallWithName = "2018-05-30T11:15:14+00:00".from(
    Known(
      "Brian", "homeController", Some("http://brian"),
      PhoneNumber("+44181811811", "+44 (181) 811811", Some("London"), NonEmptyList.of("England", "UK")))).named("fourthCallBrian")

  val fifthCall: CallWithName = "2018-05-30T13:50:19+00:00".from(Withheld).named("fifthCall")

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
          "phoneNumber" -> BSONDocument(
            "normalisedNumber" -> BSONString("+44181811811"),
            "formattedNumber" -> BSONString("+44 (181) 811811"),
            "city" -> BSONString("London"),
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
          "phoneNumber" -> BSONDocument(
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
          "phoneNumber" -> BSONDocument(
            "normalisedNumber" -> BSONString("+44181811811"),
            "formattedNumber" -> BSONString("+44 (181) 811811"),
            "city" -> BSONString("London"),
            "countries" -> BSONArray(BSONString("England"), BSONString("UK"))
          ),
          "type" -> BSONString("unknown")
        )
      )
    )
  }

  override def createDao(reactiveMongoApi: ReactiveMongoApi): MongoDbCallDao = {
    new MongoDbCallDao(reactiveMongoApi)
  }

  case class CallWithName(call: Call, maybeName: Option[String]) {
    override def toString: String = maybeName.getOrElse(call.toString)
  }

  object CallWithName {

    private val knownCalls: Seq[CallWithName] =
      Seq(firstCall, secondCall, thirdCall, fourthCall, fifthCall, secondCallBrian, fourthCallBrian)
    def apply(call: Call): CallWithName = {
      knownCalls.find(_.call == call).getOrElse(CallWithName(call, None))
    }
  }

  implicit val callWithNameToCall: CallWithName => Call = _.call

  implicit class CreateNamedCallImplicits(call: Call) {
    def named(name: String): CallWithName = CallWithName(call, Some(name))
  }

  implicit class CreateCallsImplicits(calls: Seq[Call]) {
    def withNames(): Seq[CallWithName] = calls.map(CallWithName.apply(_))
  }

  implicit class CreateCallImplicits(when: String) {
    def from(caller: Caller): Call = {
      Call(instantAt(when), caller)
    }
  }
  def instantAt(when: String): Instant = {
    OffsetDateTime.parse(when).toInstant
  }
}
