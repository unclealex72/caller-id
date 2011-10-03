package uk.co.unclealex.callerid.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class ContactsPlace extends CallerIdPlace {

	@Override
	public <T> T accept(CallerIdPlaceVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public static class Tokenizer implements PlaceTokenizer<ContactsPlace> {

		@Override
		public ContactsPlace getPlace(String token) {
			return new ContactsPlace();
		}

		@Override
		public String getToken(ContactsPlace place) {
			return "";
		}
	}
}
