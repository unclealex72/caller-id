package uk.co.unclealex.callerid.shared.model;

import java.io.Serializable;

import uk.co.unclealex.callerid.shared.visitor.PhoneNumberVisitor;

public interface PhoneNumber extends Serializable {

	public <T> T accept(PhoneNumberVisitor<T> phoneNumberVisitor);
}
