package uk.co.unclealex.callerid.remote.view

import org.junit.Test
import org.w3c.dom.Document
import static org.custommonkey.xmlunit.XMLUnit.*
import static org.junit.Assert.*
import org.codehaus.jackson.map.ObjectMapper
import java.io.InputStream
import org.w3c.dom.NodeList
import java.util.Map
import org.w3c.dom.Node
import org.w3c.dom.Attr

/**
 * Test the call.dust template.
 */
class CallDustTest extends AbstractDustTest {

    new() {
        super("call")
    }

    @Test
    def void testGeographicWithContactAndAddress() {
        "call-geographic-with-contact-and-address.json".expecting [
            time = "2012-09-05T09:12T+01:00"
            phoneNumber = "+44 1256 362362"
            contact = "Beechdown Health Club"
            location = null
            contactNumber = null
            searchTerm = null
            mapCountryCode = "GB"
            mapLocation = "Beechdown Park  Winchester Rd, Basingstoke, RG22 4ES"
        ]
    }

    @Test
    def void testGeographicWithContactButNoAddress() {
        "call-geographic-with-contact-but-no-address.json".expecting [
            time = "2012-09-05T09:12T+01:00"
            phoneNumber = "+44 1483 550550"
            contact = "University of Surrey"
            location = null
            contactNumber = null
            searchTerm = null
            mapCountryCode = "GB"
            mapLocation = "Guildford"
        ]
    }

    @Test
    def void testGeographicWithoutContact() {
        "call-geographic-without-contact.json".expecting [
            time = "2012-11-05T15:10Z"
            phoneNumber = "+44 1256 362362"
            contact = null
            location = "Basingstoke, United Kingdom"
            contactNumber = "+441256362362"
            searchTerm = "01256362362"
            mapCountryCode = "GB"
            mapLocation = "Basingstoke"
        ]
    }

    @Test
    def void testNonGeographicWithContactAndAddress() {
        "call-non-geographic-with-contact-and-address.json".expecting [
            time = "2012-09-05T09:12T+01:00"
            phoneNumber = "+1 800362362"
            contact = "American Airlines"
            location = null
            contactNumber = null
            searchTerm = null
            mapCountryCode = "US"
            mapLocation = "Los Angeles International Airport, 400 World Way, Los Angeles, CA 90045"
        ]
    }

    @Test
    def void testNonGeographicWithContactButNoAddress() {
        "call-non-geographic-with-contact-but-no-address.json".expecting [
            time = "2012-09-05T09:12T+01:00"
            phoneNumber = "+44 7012550550"
            contact = "University of Surrey"
            location = null
            contactNumber = null
            searchTerm = null
            mapCountryCode = "GB"
            mapLocation = "United Kingdom"
        ]
    }

    @Test
    def void testNonGeographicWithoutContact() {
        "call-non-geographic-without-contact.json".expecting [
            time = "2012-11-05T15:10Z"
            phoneNumber = "+33 800162362"
            contact = null
            location = "France"
            contactNumber = "+33800162362"
            searchTerm = "0800162362"
            mapCountryCode = "FR"
            mapLocation = "France"
        ]
    }

    def void expecting(String callResource, Procedures$Procedure1<Expectations> expectationBuilder) {
        val Expectations expectations = new Expectations()
        expectationBuilder.apply(expectations)
        val InputStream in = typeof(AbstractDustTest).classLoader.getResourceAsStream(callResource)
        try {
            val Call call = new ObjectMapper().readValue(in, typeof(Call))
            val Document document = renderAsXml(call)
            expectations => [
                expect(
                    document,
                    "The wrong telephone number was displayed",
                    "The wrong number of numbers were displayed",
                    "//span[@class='number']",
                    phoneNumber
                )
                expect(
                    document,
                    "The wrong contact was displayed",
                    "The wrong number of contacts were displayed",
                    "//span[@class='contact']",
                    contact
                )
                expect(
                    document,
                    "The wrong location was displayed",
                    "The wrong number of locations were displayed",
                    "//span[@class='location']",
                    location
                )
                expectButton(document, "contact",
                    if(contactNumber != null) #{"number" -> contactNumber})
                expectButton(document, "search",
                    if(searchTerm != null) #{"search-term" -> searchTerm})
                expectButton(document, "map-marker",
                    #{"country-code" -> mapCountryCode, "location" -> mapLocation})
            ]
        } finally {
            in.close
        }
    }

    def void expect(Document doc, String messageForExisting, String messageForMiscount, String xpath,
        String expectedText) {
        val matchedNodes = newXpathEngine.getMatchingNodes(xpath, doc)
        if (expectedText == null) {
            assertEquals(messageForMiscount, 0, matchedNodes.length)
        } else {
            assertEquals(messageForMiscount, 1, matchedNodes.length)
            assertEquals(messageForExisting, expectedText, matchedNodes.text)
        }
    }

    def void expectButton(Document doc, String buttonClass, Map<String, String> expectedDataParameters) {
        val matchedNodes = newXpathEngine.getMatchingNodes('''//button[@class='btn «buttonClass»']''', doc)
        assertEquals('''The wrong number of «buttonClass» buttons were found''', 1, matchedNodes.length)
        val attributes = matchedNodes.item(0).attributes
        if (expectedDataParameters == null) {
            assertNotNull('''Could not find the disabled attribute for button «buttonClass»''',
                attributes.getNamedItem("disabled"))
        } else {
            assertNull('''Button «buttonClass» was marked as disabled when it was not expected to be''',
                attributes.getNamedItem("disabled"))
            expectedDataParameters.forEach [ name, value |
                val attributeName = '''data-«name»'''
                val attribute = attributes.getNamedItem(attributeName) as Attr
                assertNotNull('''Could not find an attribute called «attributeName» on button «buttonClass»''',
                    attribute)
                assertEquals('''Attribute «attributeName» on button «buttonClass» was incorrect.''', value,
                    attribute.value)
            ]
        }
    }

    def String text(Node node) {
        node.textContent.trim.replaceAll("\\s+", " ")
    }

    def String text(NodeList nodeList) {
        nodeList.item(0).text
    }
}

/**
 * A bean used to encapsulate what is expected in each test
 */
class Expectations {
    /**
     * The expected formatted time.
     */
    @Property var String time

    /**
     * The phone number that is expected to be displayed.
     */
    @Property var String phoneNumber

    /**
     * The contact that is expected to be displayed or null if no contact is expected.
     */
    @Property var String contact

    /**
     * The location that is expected to be displayed or null if no location is expected.
     */
    @Property var String location

    /**
     * The number that is expected to be pushed onto the clipboard for adding or editing a contact or null if 
     * the contact button is expected to be disabled.
     */
    @Property var String contactNumber

    /**
     * The search term that is expected to be sent to Google or null if the search button is expected to be disabled.
     */
    @Property var String searchTerm

    /**
     * The country code used to search on Google maps.
     */
    @Property var String mapCountryCode

    /**
     * The location used to search for on Google maps.
     */
    @Property var String mapLocation
}
