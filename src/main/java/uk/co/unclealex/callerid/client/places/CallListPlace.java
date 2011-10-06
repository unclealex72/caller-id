package uk.co.unclealex.callerid.client.places;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.gwt.place.shared.PlaceTokenizer;

public class CallListPlace extends CallerIdPlace {

	private final int i_page;
	private final int i_callsPerPage;
	
	
	public CallListPlace() {
		this(0, 20);
	}

	public CallListPlace(int page, int callsPerPage) {
		super();
		i_page = page;
		i_callsPerPage = callsPerPage;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getPage(), getCallsPerPage());
	}
	
	@Override
	public boolean equals(Object obj) {
		CallListPlace other;
		return (obj instanceof CallListPlace) && getPage() == (other = (CallListPlace) obj).getPage() &&
				getCallsPerPage() == other.getCallsPerPage();
	}
	
	@Override
	public <T> T accept(CallerIdPlaceVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public int getPage() {
		return i_page;
	}
	
	public int getCallsPerPage() {
		return i_callsPerPage;
	}
	
	public static class Tokenizer implements PlaceTokenizer<CallListPlace> {

		@Override
		public CallListPlace getPlace(String token) {
			try {
				Iterator<String> iter = Splitter.on(';').split(token).iterator();
				int place = Integer.valueOf(iter.next());
				int callsPerPage = Integer.valueOf(iter.next());
				return new CallListPlace(place, callsPerPage);
			}
			catch (NumberFormatException e) {
				return new CallListPlace();
			}
			catch (NoSuchElementException e) {
				return new CallListPlace();
			}
		}

		@Override
		public String getToken(CallListPlace place) {
			return place.getPage() + ";" + place.getCallsPerPage();
		}
	}
}
