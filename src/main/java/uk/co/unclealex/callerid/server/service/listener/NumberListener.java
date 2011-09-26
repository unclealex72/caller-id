package uk.co.unclealex.callerid.server.service.listener;

import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;

public interface NumberListener {

	public boolean onRing() throws Exception;
	public boolean onNumber(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws Exception;
}
