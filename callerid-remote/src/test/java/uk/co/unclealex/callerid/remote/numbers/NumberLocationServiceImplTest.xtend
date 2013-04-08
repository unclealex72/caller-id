package uk.co.unclealex.callerid.remote.numbers

import com.google.common.collect.Iterables
import org.junit.Test

import static org.hamcrest.Matchers.*
import static org.junit.Assert.*

class NumberLocationServiceImplTest {

    @Property val extension NumberLocationServiceImpl numberLocationServiceImpl

    new() {
        this._numberLocationServiceImpl = new NumberLocationServiceImpl => [
            cityDao = new JsonResourceCityDao => [ loadCountries ]
            locationConfiguration = new LocationConfigurationBean("44", "1256")
        ]
    }

    @Test
    def void testLocal() {
        "999888".andExpect(#["United Kingdom"], "Basingstoke", "999888", "+441256999888")
    }

    @Test
    def void testNational() {
        "01483123456".andExpect(#["United Kingdom"], "Guildford", "123456", "+441483123456")
    }
    
    @Test
    def void testLocalNonGeographic() {
        "0800999888".andExpect(#["United Kingdom", "Guernsey", "Isle of Man", "Jersey"], null, "800999888", "+44800999888")
    }

    @Test
    def void testInternationalDoubleZero() {
        "003410987654".andExpect(#["Spain"], "Madrid", "987654", "+3410987654")
    }
    
    @Test
    def void testInternationalPlus() {
        "+3410987654".andExpect(#["Spain"], "Madrid", "987654", "+3410987654")
    }

    @Test
    def void testInternationalNonGeographicDoubleZero() {
        "003490987654".andExpect(#["Spain"], null, "90987654", "+3490987654")
    }

    @Test
    def void testInternationalNonGeographicPlus() {
        "+3490987654".andExpect(#["Spain"], null, "90987654", "+3490987654")
    }

    def void andExpect(String receivedNumber, Iterable<String> expectedCountryNames, String expectedCityName,
        String expectedNumber, String expectedNormalisedNumber) {
        receivedNumber.decompose => [
            assertThat("The returned phone number had the wrong countries", countries.map[name],
                contains(Iterables::toArray(expectedCountryNames, typeof(String))))
            if (expectedCityName == null) {
                assertNull("The returned phone number had a non-null city.", city)
            }
            else {
                assertEquals("The returned phone number had the wrong city.", expectedCityName, city.name)
            }
            assertEquals("The returned phone number had the wrong number.",
                expectedNumber, number)
            assertEquals("The returned phone number had the wrong normalised phone number.",
                expectedNormalisedNumber, normalisedNumber)
        ]
    }
}
