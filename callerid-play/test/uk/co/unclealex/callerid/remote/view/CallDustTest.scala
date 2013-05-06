package uk.co.unclealex.callerid.remote.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.w3c.dom.Document
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import org.w3c.dom.NodeList
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.xml.Node
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

/**
 * Test the call.dust template.
 */
class CallDustTest extends AbstractDustTest("call") with GivenWhenThen with ShouldMatchers {

  test("geographic with contact and address") {
    "call-geographic-with-contact-and-address.json" expecting Expectations(
      time = "2012-09-05T09:12T+01:00",
      phoneNumber = "+44 1256 362362",
      contact = Some("Beechdown Health Club"),
      location = None,
      contactNumber = None,
      searchTerm = None,
      mapCountryCode = "GB",
      mapLocation = "Beechdown Park  Winchester Rd, Basingstoke, RG22 4ES")
  }

  test("geographic with contact but no address") {
    "call-geographic-with-contact-but-no-address.json" expecting Expectations(
      time = "2012-09-05T09:12T+01:00",
      phoneNumber = "+44 1483 550550",
      contact = Some("University of Surrey"),
      location = None,
      contactNumber = None,
      searchTerm = None,
      mapCountryCode = "GB",
      mapLocation = "Guildford")
  }

  test("geographic without contact") {
    "call-geographic-without-contact.json" expecting Expectations(
      time = "2012-11-05T15:10Z",
      phoneNumber = "+44 1256 362362",
      contact = None,
      location = Some("Basingstoke, United Kingdom"),
      contactNumber = Some("+441256362362"),
      searchTerm = Some("01256362362"),
      mapCountryCode = "GB",
      mapLocation = "Basingstoke")
  }

  test("non geographic with contact and address") {
    "call-non-geographic-with-contact-and-address.json" expecting Expectations(
      time = "2012-09-05T09:12T+01:00",
      phoneNumber = "+1 800362362",
      contact = Some("American Airlines"),
      location = None,
      contactNumber = None,
      searchTerm = None,
      mapCountryCode = "US",
      mapLocation = "Los Angeles International Airport, 400 World Way, Los Angeles, CA 90045")
  }

  test("non geographic with contact but no address") {
    "call-non-geographic-with-contact-but-no-address.json" expecting Expectations(
      time = "2012-09-05T09:12T+01:00",
      phoneNumber = "+44 7012550550",
      contact = Some("University of Surrey"),
      location = None,
      contactNumber = None,
      searchTerm = None,
      mapCountryCode = "GB",
      mapLocation = "United Kingdom")
  }

  test("non geographic without contact") {
    "call-non-geographic-without-contact.json" expecting Expectations(
      time = "2012-11-05T15:10Z",
      phoneNumber = "+33 800162362",
      contact = None,
      location = Some("France"),
      contactNumber = Some("+33800162362"),
      searchTerm = Some("0800162362"),
      mapCountryCode = "FR",
      mapLocation = "France")
  }

  implicit class TestCase(jsonResource: String) {
    def expecting(expectations: Expectations): Unit = {
      val call =
        new ObjectMapper().registerModule(DefaultScalaModule).
          readValue(getClass.getClassLoader.getResource(jsonResource), classOf[Call])
      val root = renderAsXml(call)
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
