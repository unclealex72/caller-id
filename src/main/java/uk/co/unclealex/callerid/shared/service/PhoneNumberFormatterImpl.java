package uk.co.unclealex.callerid.shared.service;

import uk.co.unclealex.callerid.shared.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.shared.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;
import uk.co.unclealex.callerid.shared.visitor.PhoneNumberVisitor;

import com.google.common.base.Joiner;

public class PhoneNumberFormatterImpl implements PhoneNumberFormatter {

	@Override
	public String prettyPrintNumber(PhoneNumber phoneNumber) {
		return new NumberPrettyPrinter().prettyPrint(phoneNumber);
	}

	@Override
	public String prettyPrintGeographicInformation(PhoneNumber phoneNumber) {
		return new GeographicPrettyPrinter().prettyPrint(phoneNumber);
	}

	@Override
	public String formatForSearch(PhoneNumber phoneNumber) {
		return new SearchPrettyPrinter().prettyPrint(phoneNumber);
	}
	
	abstract class PrettyPrinter implements PhoneNumberVisitor<String> {
		
		public String prettyPrint(PhoneNumber phoneNumber) {
			return phoneNumber.accept(this);
		}
	}
	
	class NumberPrettyPrinter extends PrettyPrinter {
		@Override
		public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
			String countryCode = countriesOnlyPhoneNumber.getCountryCode();
			String number = countriesOnlyPhoneNumber.getNumber();
			if (Constants.UK.equals(countryCode)) {
				return "0" + number;
			}
			else {
				return Joiner.on("").join("+", countryCode, " ", number);
			}
		}
		
		@Override
		public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
			String countryCode = countryAndAreaPhoneNumber.getCountryCode();
			String areaCode = countryAndAreaPhoneNumber.getAreaCode();
			String number = countryAndAreaPhoneNumber.getNumber();
			if (Constants.UK.equals(countryCode)) {
				if (Constants.BASINGSTOKE.equals(areaCode)) {
					return number;
				}
				else {
					return Joiner.on("").join("0", areaCode, " ", number);
				}
			}
			else {
				return Joiner.on("").join("+", countryCode, " ", areaCode, number);
			}
		}
		
		@Override
		public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
			return numberOnlyPhoneNumber.getNumber();
		}
		
		@Override
		public String visit(PhoneNumber phoneNumber) {
			return phoneNumber.toString();
		}
	}
	
	class GeographicPrettyPrinter extends PrettyPrinter {

		@Override
		public String visit(PhoneNumber phoneNumber) {
			return null;
		}

		@Override
		public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
			return null;
		}

		@Override
		public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
			return countriesOnlyPhoneNumber.getCountries().get(0);
		}

		@Override
		public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
			return countryAndAreaPhoneNumber.getArea() + ", " + countryAndAreaPhoneNumber.getCountry();
		}
	}
	
	class SearchPrettyPrinter extends PrettyPrinter {

		@Override
		public String visit(PhoneNumber phoneNumber) {
			return null;
		}

		@Override
		public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
			return numberOnlyPhoneNumber.getNumber();
		}

		@Override
		public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
			return asNumber(countriesOnlyPhoneNumber.getCountryCode(), countriesOnlyPhoneNumber.getNumber());
		}

		@Override
		public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
			return asNumber(
					countryAndAreaPhoneNumber.getCountryCode(), 
					countryAndAreaPhoneNumber.getAreaCode(), countryAndAreaPhoneNumber.getNumber());
		}
		
		public String asNumber(String countryCode, String... numbers) {
			StringBuilder builder = new StringBuilder();
			builder.append('0');
			if (!Constants.UK.equals(countryCode)) {
				builder.append('0').append(countryCode);
			}
			Joiner.on("").appendTo(builder, numbers);
			return builder.toString();
		}
	}

}
