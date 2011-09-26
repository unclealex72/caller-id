package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import uk.co.unclealex.callerid.shared.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;

public class NumberLocationServiceTest {

	private static final Logger log = LoggerFactory.getLogger(NumberLocationServiceTest.class);
	
	@Test
	public void testTrailingNumbers() {
		Assert.assertEquals("Trailing numbers failed.", "1256", new NumberLocationServiceImpl().trailingNumbers("(0) 1256"));
	}
	
	@Test
	public void testLocalNumber() {
		test("460837", new CountryAndAreaPhoneNumber("United Kingdom", "Basingstoke", "44", "1256", "460837"));
	}

	@Test
	public void testNationalNumber() {
		test("01483654321", new CountryAndAreaPhoneNumber("United Kingdom", "Guildford", "44", "1483", "654321"));
	}

	@Test
	public void testMobileNumber() {
		test(
			"07808721396",
			new CountriesOnlyPhoneNumber(
				Sets.newTreeSet(Arrays.asList(new String[] {"United Kingdom", "Guernsey", "Jersey", "Isle of Man"})),
				"44", "7808721396"));
	}

	@Test
	public void testPlusInternationalNumber() {
		test("+33359123456", new CountryAndAreaPhoneNumber("France", "Nord Pas-de-Calais", "33", "359", "123456"));
	}

	@Test
	public void testDoubleZeroInternationalNumber() {
		test("0033359123456", new CountryAndAreaPhoneNumber("France", "Nord Pas-de-Calais", "33", "359", "123456"));
	}

	@Test
	public void testInternationalNonGeographicNumber() {
		test(
			"001800555666",
			new CountriesOnlyPhoneNumber(
					Sets.newTreeSet(Arrays.asList(new String[] {
							"United States", "Canada", "Dominican Republic", "Puerto Rico", "American Samoa", "Anguilla",
							"Antigua and Barbuda", "Bahamas", "Barbados", "Bermuda", "Cayman Islands", "Dominica", "Grenada", 
							"Guam", "Jamaica", "Montserrat", "Northern Mariana Islands", "Saint Kitts and Nevis", 
							"Saint Lucia", "Saint Vincent and the Grenadines", "Trinidad and Tobago", 
							"Turks and Caicos Islands", "Virgin Islands- British", "Virgin Islands- U.S."})),
					"1", "800555666"));

	}

	protected void test(String number, PhoneNumber expectedPhoneNumber) {
		NumberLocationServiceImpl numberLocationService = new NumberLocationServiceImpl();
		try {
			numberLocationService.initialise();
		}
		catch (IOException e) {
			log.error("The service faile to initialise.", e);
			Assert.fail("The service failed to initialise: " + e.getMessage());
		}
		PhoneNumber actualPhoneNumber = numberLocationService.decomposeNumber(number);
		Assert.assertEquals(
				"Phone number " + number + " did not decompose properly.", expectedPhoneNumber, actualPhoneNumber);
	}
}
