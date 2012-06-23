package uk.co.unclealex.callerid.server.service;

import uk.co.unclealex.callerid.server.model.TelephoneNumber;

public interface TelephoneNumberFactory {

	public TelephoneNumber findOrCreateTelephoneNumber(String number);
}
