package number

import cats.data.Validated.Valid
import cats.data._
import org.scalatest._

class PhoneNumberFactoryImplTest extends WordSpec with Matchers {

  val cityDao = new CityDaoImpl(Countries().countries)
  val localService = new LocalServiceImpl(internationalCode = "44", stdCode = "1256")
  val numberFormatter: NumberFormatter = (countries: NonEmptyList[Country], maybeCity: Option[City], number: String) => {
    s"_${countries.head.internationalDiallingCode}${maybeCity.map(_.stdCode).getOrElse("")}${number}_"
  }
  val phoneNumberFactory = new PhoneNumberFactoryImpl(cityDao, numberFormatter, localService)

  "Spaces" should {
    "be ignored" in {
      " +34  10 987654 ".toLocation should ===(Valid((Some("Madrid"), "+3410987654", "_3410987654_", Seq("Spain"))))
    }
  }

  "A local number" should {
    "have the local city and country" in {
      "999888".toLocation should ===(Valid((Some("Basingstoke"), "+441256999888", "_441256999888_", Seq("United Kingdom"))))
    }
  }

  "A national number" should {
    "have the city and country" in {
      "01483123456".toLocation should ===(Valid((Some("Guildford"), "+441483123456", "_441483123456_", Seq("United Kingdom"))))
    }
  }

  "A local non-geographic number" should {
    "have no city but all the possible countries" in {
      "0800999888".toLocation should ===(
        Valid((None, "+44800999888", "_44800999888_", Seq("United Kingdom", "Guernsey", "Isle of Man", "Jersey"))))
    }
  }

  "An international number with a 00 prefix" should {
    "have a city and one country" in {
      "003410987654".toLocation should ===(Valid((Some("Madrid"), "+3410987654", "_3410987654_", Seq("Spain"))))
    }
  }

  "An international number with a + prefix" should {
    "have a city and one country" in {
      "+3410987654".toLocation should ===(Valid((Some("Madrid"), "+3410987654", "_3410987654_", Seq("Spain"))))
    }
  }

  "An international non-geographic number with a 00 prefix" should {
    "have a country but no city" in {
      "003490987654".toLocation should ===(Valid((None, "+3490987654", "_3490987654_", Seq("Spain"))))
    }
  }

  "An international non-geographic number with a + prefix" should {
    "have a country but no city" in {
      "+3490987654".toLocation should ===(Valid((None, "+3490987654", "_3490987654_", Seq("Spain"))))
    }
  }

  implicit class TestCase(receivedNumber: String) {
    def toLocation: ValidatedNel[String, (Option[String], String, String, Seq[String])] = {
      val phoneNumber = phoneNumberFactory.decompose(receivedNumber)
      phoneNumber.map { phoneNumber =>
        (phoneNumber.city, phoneNumber.normalisedNumber, phoneNumber.formattedNumber, phoneNumber.countries.toList)
      }
    }
  }
}

