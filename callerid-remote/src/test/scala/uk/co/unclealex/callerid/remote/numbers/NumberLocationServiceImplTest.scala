package uk.co.unclealex.callerid.remote.numbers

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

class NumberLocationServiceImplTest extends FunSuite with ShouldMatchers {

  implicit class TestCase(receivedNumber: String) {
    def decomposesTo(expectedCityName: Option[String], expectedNumber: String, expectedNormalisedNumber: String, expectedCountryNames: String*) = {
      val phoneNumber = numberLocationServiceImpl.decompose(receivedNumber)
      phoneNumber.countries.map(_.name).toList should equal(expectedCountryNames.toList)
      phoneNumber.city.map(_.name) should equal(expectedCityName)
      phoneNumber.number should equal(expectedNumber)
      phoneNumber.normalisedNumber should equal(expectedNormalisedNumber)
    }

  }
  val cityDao = new JsonResourceCityDao
  val numberLocationServiceImpl = new NumberLocationServiceImpl(cityDao, new LocationConfiguration("44", "1256"))

  test("local number") {
    "999888" decomposesTo (Some("Basingstoke"), "999888", "+441256999888", "United Kingdom")
  }

  test("national number") {
    "01483123456" decomposesTo (Some("Guildford"), "123456", "+441483123456", "United Kingdom")
  }

  test("local non-geographic number") {
    "0800999888" decomposesTo (None, "800999888", "+44800999888", "United Kingdom", "Guernsey", "Isle of Man", "Jersey")
  }

  test("international number with a 00 prefix") {
    "003410987654" decomposesTo (Some("Madrid"), "987654", "+3410987654", "Spain")
  }

  test("international number with a + prefix") {
    "+3410987654" decomposesTo (Some("Madrid"), "987654", "+3410987654", "Spain")
  }

  test("international non-geographic with a 00 prefix") {
    "003490987654" decomposesTo (None, "90987654", "+3490987654", "Spain")
  }

  test("international non-geographic with a + prefix") {
    "+3490987654" decomposesTo (None, "90987654", "+3490987654", "Spain")
  }

}

