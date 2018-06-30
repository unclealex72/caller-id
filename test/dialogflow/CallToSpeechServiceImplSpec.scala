package dialogflow

import java.time.{Instant, OffsetDateTime, ZoneId}

import call._
import cats.data.NonEmptyList
import datetime.DaySuffixesImpl
import number.PhoneNumber
import org.scalatest.{Matchers, WordSpec}

class CallToSpeechServiceImplSpec extends WordSpec with Matchers {

  val callToSpeechService = new CallToSpeechServiceImpl(
    new WebhookResponseDateTimeFormatter(DaySuffixesImpl)(), ZoneId.of("Europe/London"))
  val now: Long = OffsetDateTime.parse("2018-05-28T11:09:28+01:00").toInstant.toEpochMilli

  def speak(caller: Caller): String = {
    callToSpeechService.speak(Call(now, caller))
  }

  "An unknown non-geographic call" should {
    "speak the number and the list of possible countries" in {
      speak(
        Unknown(
          PhoneNumber(
            "+448001234567",
            "0800 1234567",
            None,
            NonEmptyList.of("UK", "Guernsey")))) should === (
        "There was a call from 0800 1234567 in UK or Guernsey on Monday the 28th of May at 11 09 a m")
    }
  }

  "An unknown geographic call" should {
    "speak the number and the city and country of origin" in {
      speak(
        Unknown(
          PhoneNumber(
            "+441483234567",
            "01483 1234567",
            Some("Guildford"),
            NonEmptyList.one("UK")))) should === (
        "There was a call from 01483 1234567 in Guildford, UK on Monday the 28th of May at 11 09 a m")
    }
  }

  "An call from a contact" should {
    "speak the name of the contact" in {
      speak(
        Known(
          "Freddie",
          "something",
          None,
          PhoneNumber(
            "+441483234567",
            "01483 1234567",
            Some("Guildford"),
            NonEmptyList.one("UK")))) should === (
        "Freddie called on Monday the 28th of May at 11 09 a m")
    }
  }
}
