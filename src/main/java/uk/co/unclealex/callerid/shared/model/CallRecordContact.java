package uk.co.unclealex.callerid.shared.model;

import java.io.Serializable;

public class CallRecordContact implements Serializable, Comparable<CallRecordContact> {

	private String i_name;
	private boolean i_googleContact;
	
	protected CallRecordContact() {
		super();
	}

	public CallRecordContact(String name, boolean googleContact) {
		super();
		i_name = name;
		i_googleContact = googleContact;
	}

	@Override
	public int compareTo(CallRecordContact o) {
		return getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CallRecordContact && compareTo((CallRecordContact) obj) == 0;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	public String getName() {
		return i_name;
	}

	public boolean isGoogleContact() {
		return i_googleContact;
	}	
}
