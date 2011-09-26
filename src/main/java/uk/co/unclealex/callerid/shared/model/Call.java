package uk.co.unclealex.callerid.shared.model;

import java.util.Date;

import uk.co.unclealex.callerid.server.model.TelephoneNumber;

public class Call {

	private final Date i_callTime;
	private final TelephoneNumber i_telephoneNumber;
	private final String i_contact;

	public Call(Date callTime, TelephoneNumber telephoneNumber, String contact) {
		super();
		i_callTime = callTime;
		i_telephoneNumber = telephoneNumber;
		i_contact = contact;
	}

	public Date getCallTime() {
		return i_callTime;
	}

	public TelephoneNumber getTelephoneNumber() {
		return i_telephoneNumber;
	}

	public String getContact() {
		return i_contact;
	}
}
