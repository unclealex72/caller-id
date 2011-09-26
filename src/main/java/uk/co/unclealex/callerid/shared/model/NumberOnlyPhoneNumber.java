package uk.co.unclealex.callerid.shared.model;

import uk.co.unclealex.callerid.shared.visitor.PhoneNumberVisitor;

public class NumberOnlyPhoneNumber implements PhoneNumber {

	private String i_number;
	
	protected NumberOnlyPhoneNumber() {
		super();
		// Default constructor for serialisation
	}

	public NumberOnlyPhoneNumber(String number) {
		super();
		i_number = number;
	}

	@Override
	public String toString() {
		return getNumber();
	}
	
	@Override
	public <T> T accept(PhoneNumberVisitor<T> phoneNumberVisitor) {
		return phoneNumberVisitor.visit(this);
	}

	public String getNumber() {
		return i_number;
	}
}
