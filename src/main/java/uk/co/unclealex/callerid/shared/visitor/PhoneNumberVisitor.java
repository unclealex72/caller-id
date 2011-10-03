package uk.co.unclealex.callerid.shared.visitor;

import java.io.Serializable;

import uk.co.unclealex.callerid.shared.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.shared.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;

public interface PhoneNumberVisitor<T> extends Serializable {

	T visit(PhoneNumber phoneNumber);
	T visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber);
	T visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber);
	T visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber);

}
