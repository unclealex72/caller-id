package uk.co.unclealex.callerid.shared.service;

import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

public interface PhoneNumberFormatter {

	public String prettyPrintNumber(PhoneNumber phoneNumber);
	
	public String prettyPrintGeographicInformation(PhoneNumber phoneNumber);

	public String formatForSearch(PhoneNumber phoneNumber);
}
