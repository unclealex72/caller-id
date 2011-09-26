package uk.co.unclealex.callerid.shared.visitor;

import uk.co.unclealex.callerid.shared.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.shared.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;

public interface PhoneNumberVisitor<T> {

	T visit(PhoneNumber phoneNumber);
	T visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber);
	T visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber);
	T visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber);

}
