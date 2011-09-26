package uk.co.unclealex.callerid.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class CallListPlace extends CallerIdPlace {

	@Override
	public boolean isEqual(Object callListPlace) {
		return true;
	}

	@Override
	public <T> T accept(CallerIdPlaceVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public static class Tokenizer implements PlaceTokenizer<CallListPlace> {

		@Override
		public CallListPlace getPlace(String token) {
			return new CallListPlace();
		}

		@Override
		public String getToken(CallListPlace place) {
			return "";
		}
	}
}
