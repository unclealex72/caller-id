package uk.co.unclealex.callerid.shared.model;

import java.util.Date;

import uk.co.unclealex.callerid.server.model.TelephoneNumber;

public class Call {

	private final Date i_callTime;
	private final PhoneNumber i_phoneNumber;
	private final String i_contact;

	public Call(Date callTime, PhoneNumber phoneNumber, String contact) {
		super();
		i_callTime = callTime;
		i_phoneNumber = phoneNumber;
		i_contact = contact;
	}

	public Date getCallTime() {
		return i_callTime;
	}

	public PhoneNumber getPhoneNumber() {
		return i_phoneNumber;
	}

	public String getContact() {
		return i_contact;
	}
}
