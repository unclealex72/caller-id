package uk.co.unclealex.callerid.shared.model;

import uk.co.unclealex.callerid.shared.visitor.PhoneNumberVisitor;

public class CountryAndAreaPhoneNumber implements PhoneNumber {

	private String i_country;
	private String i_area;
	private String i_countryCode;
	private String i_areaCode;
	private String i_number;
	
	protected CountryAndAreaPhoneNumber() {
		super();
		// Default constructor for serialisation
	}

	public CountryAndAreaPhoneNumber(String country, String area, String countryCode, String areaCode, String number) {
		super();
		i_country = country;
		i_area = area;
		i_countryCode = countryCode;
		i_areaCode = areaCode;
		i_number = number;
	}

	@Override
	public boolean equals(Object obj) {
		CountryAndAreaPhoneNumber other;
		return (obj instanceof CountryAndAreaPhoneNumber) &&
			getCountry().equals(((other = (CountryAndAreaPhoneNumber) obj).getCountry())) &&
			getCountryCode().equals(other.getCountryCode()) &&
			getArea().equals(other.getArea()) &&
			getAreaCode().equals(other.getAreaCode()) &&
			getNumber().equals(other.getNumber());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder('+');
		builder.
			append(getCountryCode()).
			append(' ').
			append(getAreaCode()).
			append(' ').
			append(getNumber()).
			append(" (").
			append(getArea()).
			append(", ").
			append(getCountry()).
			append(")");
		return builder.toString();
	}
	
	@Override
	public <T> T accept(PhoneNumberVisitor<T> phoneNumberVisitor) {
		return phoneNumberVisitor.visit(this);
	}

	public String getCountry() {
		return i_country;
	}

	public String getArea() {
		return i_area;
	}

	public String getCountryCode() {
		return i_countryCode;
	}

	public String getAreaCode() {
		return i_areaCode;
	}

	public String getNumber() {
		return i_number;
	}

}
