package call

import java.time.{Clock, Instant, ZoneId}

import cats.data._
import cats.implicits._
import contact.{Contact, ContactDao, User}
import number.{City, Country, PhoneNumberFactory, PhoneNumber}
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.collection.SortedSet
import scala.concurrent.{ExecutionContext, Future}

class CallServiceImplSpec extends AsyncWordSpec with Matchers {

  val clock: Clock = Clock.fixed(Instant.parse("2018-05-20T15:39:00Z"), ZoneId.of("Europe/London"))
  val now: Instant = clock.instant()

  val London: City = City("London", "181")
  val UK: Country = Country("United Kingdom", "44", "GB", None, SortedSet(London))

  val knownPhoneNumber: PhoneNumber =
    PhoneNumber("+441818118181", "+44 (181) 8118181", Some("London"), NonEmptyList.one("UK"))
  val unknownPhoneNumber: PhoneNumber =
    PhoneNumber("+441818228282", "+44 (181) 8118181", Some("London"), NonEmptyList.one("UK"))
  val numberLocationService: PhoneNumberFactory = (number: String) =>
    Seq(knownPhoneNumber, unknownPhoneNumber).
      find(_.normalisedNumber == number).
      toValidNel("Don't know that phone number!")

  val BBC = Contact("+441818118181", "The BBC", "main", Some("http://bbc"))
  val contactsService: ContactDao = new ContactDao {
    override def upsertUser(user: User): Future[Either[Seq[String], Unit]] = {
      throw new NotImplementedError("upsertUser")
    }
    override def findContactNameAndPhoneTypeForPhoneNumber(normalisedPhoneNumber: String): Future[Option[Contact]] = {
      Future.successful(Seq(BBC).find(_.normalisedPhoneNumber == normalisedPhoneNumber))
    }
  }

  val callService = new CallServiceImpl(clock, numberLocationService, contactsService)

  def callOf(caller: Caller): Option[Call] = Some(Call(now, caller))

  "OK responses" should {
    "be ignored" in {
      callService.call(modem.Ok).map { response =>
        response should ===(None)
      }
    }
  }

  "RING responses" should {
    "be ignored" in {
      callService.call(modem.Ring).map { response =>
        response should ===(None)
      }
    }
  }

  "WITHHELD responses" should {
    "show up as withheld" in {
      callService.call(modem.Withheld).map { response =>
        response should ===(callOf(Withheld))
      }
    }
  }

  "UNKNOWN responses" should {
    "show up as undefinable" in {
      callService.call(modem.Unknown("xyz")).map { response =>
        response should ===(callOf(Undefinable("xyz")))
      }
    }
  }

  "Contacts' telephone numbers" should {
    "identify the contact" in {
      callService.call(modem.Number("+441818118181")).map { response =>
        response should ===(callOf(Known("The BBC", "main", Some("http://bbc"), knownPhoneNumber)))
      }
    }
  }

  "Unknown telephone numbers" should {
    "echo the phone number" in {
      callService.call(modem.Number("+441818228282")).map { response =>
        response should ===(callOf(Unknown(unknownPhoneNumber)))
      }
    }
  }

  "Unparseable telephone numbers" should {
    "show up as undefinable" in {
      callService.call(modem.Number("+441818338383")).map { response =>
        response should ===(callOf(Undefinable("+441818338383")))
      }
    }
  }
}
