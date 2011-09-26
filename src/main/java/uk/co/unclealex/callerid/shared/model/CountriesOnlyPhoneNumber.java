package uk.co.unclealex.callerid.shared.model;

import java.util.Set;

import uk.co.unclealex.callerid.shared.visitor.PhoneNumberVisitor;

import com.google.common.base.Joiner;

public class CountriesOnlyPhoneNumber implements PhoneNumber {

	private Set<String> i_countries;
	private String i_countryCode;
	private String i_number;
	
	public CountriesOnlyPhoneNumber() {
		super();
		// Default constructor for serialisation.
	}

	public CountriesOnlyPhoneNumber(Set<String> countries, String countryCode, String number) {
		super();
		i_countries = countries;
		i_countryCode = countryCode;
		i_number = number;
	}

	@Override
	public boolean equals(Object obj) {
		CountriesOnlyPhoneNumber other;
		return (obj instanceof CountriesOnlyPhoneNumber) &&
			getCountries().equals(((other = (CountriesOnlyPhoneNumber) obj).getCountries())) &&
			getCountryCode().equals(other.getCountryCode()) &&
			getNumber().equals(other.getNumber());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder('+');
		builder.
			append(getCountryCode()).
			append(' ').
			append(getNumber()).
			append(" (").
			append(Joiner.on(", ").join(getCountries())).
			append(")");
		return builder.toString();
	}
	
	@Override
	public <T> T accept(PhoneNumberVisitor<T> phoneNumberVisitor) {
		return phoneNumberVisitor.visit(this);
	}

	public Set<String> getCountries() {
		return i_countries;
	}

	public String getCountryCode() {
		return i_countryCode;
	}

	public String getNumber() {
		return i_number;
	}

}
