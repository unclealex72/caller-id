package number

import cats.data.Validated.Valid
import org.scalatest._
import cats.data._

class NumberLocationServiceImplTest extends WordSpec with Matchers {

  val cityDao = new CityDaoImpl(Countries().countries)
  val numberLocationServiceImpl = new NumberLocationServiceImpl(cityDao, new LocationConfiguration("44", "1256"))

  "Spaces" should {
    "be ignored" in {
      " +34  10 987654 ".toLocation should ===(Valid((Some("Madrid"), "987654", "+3410987654", Seq("Spain"))))
    }
  }

  "A local number" should {
    "have the local city and country" in {
      "999888".toLocation should ===(Valid((Some("Basingstoke"), "999888", "+441256999888", Seq("United Kingdom"))))
    }
  }

  "A national number" should {
    "have the city and country" in {
      "01483123456".toLocation should ===(Valid((Some("Guildford"), "123456", "+441483123456", Seq("United Kingdom"))))
    }
  }

  "A local non-geographic number" should {
    "have no city but all the possible countries" in {
      "0800999888".toLocation should ===(
        Valid((None, "800999888", "+44800999888", Seq("United Kingdom", "Guernsey", "Isle of Man", "Jersey"))))
    }
  }

  "An international number with a 00 prefix" should {
    "have a city and one country" in {
      "003410987654".toLocation should ===(Valid((Some("Madrid"), "987654", "+3410987654", Seq("Spain"))))
    }
  }

  "An international number with a + prefix" should {
    "have a city and one country" in {
      "+3410987654".toLocation should ===(Valid((Some("Madrid"), "987654", "+3410987654", Seq("Spain"))))
    }
  }

  "An international non-geographic number with a 00 prefix" should {
    "have a country but no city" in {
      "003490987654".toLocation should ===(Valid((None, "90987654", "+3490987654", Seq("Spain"))))
    }
  }

  "An international non-geographic number with a + prefix" should {
    "have a country but no city" in {
      "+3490987654".toLocation should ===(Valid((None, "90987654", "+3490987654", Seq("Spain"))))
    }
  }

  implicit class TestCase(receivedNumber: String) {
    def toLocation: ValidatedNel[String, (Option[String], String, String, Seq[String])] = {
      val phoneNumber = numberLocationServiceImpl.decompose(receivedNumber)
      phoneNumber.map { phoneNumber =>
        (phoneNumber.city.map(_.name), phoneNumber.number, phoneNumber.normalisedNumber, phoneNumber.countries.map(_.name).toList)
      }
    }
  }
}

