package uk.co.unclealex.callerid.server.service.listener;

import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;

public interface NumberListener {

	public boolean onRing() throws Exception;
	public boolean onNumber(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws Exception;
}
