package uk.co.unclealex.callerid.remote.view

import java.util.Date
import scala.xml.Elem
import scala.xml.Node
import scala.xml.NodeSeq
import scala.xml.XML
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers
import scalaz._
import uk.co.unclealex.callerid.remote.call.ReceivedCall
import uk.co.unclealex.callerid.remote.number.City
import uk.co.unclealex.callerid.remote.number.Country
import uk.co.unclealex.callerid.remote.number.LocationConfiguration
import uk.co.unclealex.callerid.remote.number.NumberFormatterImpl
import uk.co.unclealex.callerid.remote.number.PhoneNumber
import uk.co.unclealex.callerid.remote.contact.Contact

/**
 * Test the call.dust template.
 */
class CallViewTest extends FunSuite with GivenWhenThen with ShouldMatchers {

  val uk = NonEmptyList(Country("United Kingdom", "44", "gb", List()))
  val us = NonEmptyList(Country("United States of America", "1", "us", List()))
  val basingstoke = Some(City("Basingstoke", "1256"))
  val guildford = Some(City("Guildford", "1483"))
  val france = NonEmptyList(Country("France", "33", "fr", List()))

  test("geographic with contact and address") {
    ReceivedCall(
      at("2012-09-05T09:12T+01:00"),
      PhoneNumber("+441256362362", uk, basingstoke, "362362"),
      Some(Contact("Beechdown Health Club", Some("Beechdown Park  Winchester Rd, Basingstoke, RG22 4ES")))) expecting
      Expectations(
        time = "2012-09-05T09:12T+01:00",
        phoneNumber = "+44 1256 362362",
        contact = Some("Beechdown Health Club"),
        location = None,
        contactNumber = None,
        searchTerm = None,
        mapCountryCode = "gb",
        mapLocation = "Beechdown Park  Winchester Rd, Basingstoke, RG22 4ES")
  }

  test("geographic with contact but no address") {
    ReceivedCall(
      at("2012-09-05T09:12T+01:00"),
      PhoneNumber("+441483550550", uk, guildford, "550550"),
      Some(Contact("University of Surrey", None))) expecting Expectations(
        time = "2012-09-05T09:12T+01:00",
        phoneNumber = "+44 1483 550550",
        contact = Some("University of Surrey"),
        location = None,
        contactNumber = None,
        searchTerm = None,
        mapCountryCode = "gb",
        mapLocation = "Guildford")
  }

  test("geographic without contact") {
    ReceivedCall(
      at("2012-11-05T15:10Z"),
      PhoneNumber("+441256362362", uk, basingstoke, "362362"),
      None) expecting Expectations(
        time = "2012-11-05T15:10Z",
        phoneNumber = "+44 1256 362362",
        contact = None,
        location = Some("Basingstoke, United Kingdom"),
        contactNumber = Some("+441256362362"),
        searchTerm = Some("01256362362"),
        mapCountryCode = "gb",
        mapLocation = "Basingstoke")
  }

  test("non geographic with contact and address") {
    ReceivedCall(
      at("2012-09-05T09:12T+01:00"),
      PhoneNumber("+1800362362", us, None, "800362362"),
      Some(Contact(
        "American Airlines",
        Some("Los Angeles International Airport, 400 World Way, Los Angeles, CA 90045")))) expecting Expectations(
        time = "2012-09-05T09:12T+01:00",
        phoneNumber = "+1 800362362",
        contact = Some("American Airlines"),
        location = None,
        contactNumber = None,
        searchTerm = None,
        mapCountryCode = "us",
        mapLocation = "Los Angeles International Airport, 400 World Way, Los Angeles, CA 90045")
  }

  test("non geographic with contact but no address") {
    ReceivedCall(
      at("2012-09-05T09:12T+01:00"),
      PhoneNumber("+447012550550", uk, None, "7012550550"),
      Some(Contact("University of Surrey", None))) expecting Expectations(
        time = "2012-09-05T09:12T+01:00",
        phoneNumber = "+44 7012550550",
        contact = Some("University of Surrey"),
        location = None,
        contactNumber = None,
        searchTerm = None,
        mapCountryCode = "gb",
        mapLocation = "United Kingdom")
  }

  test("non geographic without contact") {
    ReceivedCall(
      at("2012-11-05T15:10Z"),
      PhoneNumber("+33800162362", france, None, "800162362"),
      None) expecting Expectations(
        time = "2012-11-05T15:10Z",
        phoneNumber = "+33 800162362",
        contact = None,
        location = Some("France"),
        contactNumber = Some("+33800162362"),
        searchTerm = Some("0800162362"),
        mapCountryCode = "fr",
        mapLocation = "France")
  }

  def at(formattedDate: String): Date = {
    null
  }

  implicit class TestCase(receivedCall: ReceivedCall) {
    def expecting(expectations: Expectations): Unit = {
      val numberFormatter = new NumberFormatterImpl(new LocationConfiguration("44", "1256"))
      val phoneNumber = receivedCall.phoneNumber
      val template = views.html.call.apply(
        phoneNumber,
        numberFormatter.formatNumberAsInternational(phoneNumber),
        numberFormatter.formatAddress(phoneNumber),
        receivedCall.contact).toString
      val root = XML.loadString(template)
      expect(
        "telephone number",
        root \\ "span" find { _ \ "@class" find { _.text == "number" } isDefined },
        Some(expectations.phoneNumber))
      expect(
        "contact",
        root \\ "span" find { _ \ "@class" find { _.text == "contact" } isDefined },
        expectations.contact)
      expect(
        "location",
        root \\ "span" find { _ \ "@class" find { _.text == "location" } isDefined },
        expectations.location)
      expectButton(root, "contact",
        expectations.contactNumber.map(contactNumber => Map("number" -> contactNumber)))
      expectButton(root, "search",
        expectations.searchTerm.map(searchTerm => Map("search-term" -> searchTerm)))
      expectButton(root, "map-marker",
        Some(Map("country-code" -> expectations.mapCountryCode, "location" -> expectations.mapLocation)))
    }

    def expect(objectName: String, expectedNode: Option[Node], expectedText: Option[String]) = {
      expectedText.map {
        expectedText =>
          When(s"Expecting exactly one ${objectName}")
          expectedNode should not be None
          expectedNode.get.text.trim.replaceAll("\\s+", " ") should equal(expectedText.trim.replaceAll("\\s+", " "))
      }.getOrElse {
        When(s"Expecting no ${objectName}")
        expectedNode should be(None)
      }
    }

    def expectButton(root: Elem, buttonClass: String, expectedDataParameters: Option[Map[String, String]]) = {
      val matchedNode = root \\ "button" find { _ \ "@class" find { _.text == s"btn ${buttonClass}" } isDefined }
      When(s"searching for a button with class ${buttonClass}")
      matchedNode should not be None
      val attributes = matchedNode.get.attributes
      expectedDataParameters.map {
        expectedDataParameters =>
          Then("the button should not be disabled")
          attributes.get("disabled") should equal(None)
          expectedDataParameters.foreach {
            case (name, value) =>
              val attributeName = s"data-${name}"
              Then(s"it should have an attribute called ${attributeName}")
              attributes.get(attributeName).map { _.text } should equal(Some(value))
          }
      }.getOrElse {
        Then("the button should not disabled")
        attributes.get("disabled") should not equal (None)
      }
    }
  }

}

/**
 * A bean used to encapsulate what is expected in each test
 */
case class Expectations(
  /**
   * The expected formatted time.
   */
  time: String,
  /**
   * The phone number that is expected to be displayed.
   */
  phoneNumber: String,
  /**
   * The contact that is expected to be displayed or None if no contact is expected.
   */
  contact: Option[String],
  /**
   * The location that is expected to be displayed or None if no location is expected.
   */
  location: Option[String],
  /**
   * The number that is expected to be pushed onto the clipboard for adding or editing a contact or None if
   * the contact button is expected to be disabled.
   */
  contactNumber: Option[String],
  /**
   * The search term that is expected to be sent to Google or None if the search button is expected to be disabled.
   */
  searchTerm: Option[String],
  /**
   * The country code used to search on Google maps.
   */
  mapCountryCode: String,
  /**
   * The location used to search for on Google maps.
   */
  mapLocation: String)
