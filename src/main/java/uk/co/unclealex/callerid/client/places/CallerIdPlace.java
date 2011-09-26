package uk.co.unclealex.callerid.client.places;

import com.google.gwt.place.shared.Place;

public abstract class CallerIdPlace extends Place {

	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass().equals(obj.getClass()) && isEqual(obj);
	}
	
	public abstract boolean isEqual(Object otherPlace);
	
	public abstract <T> T accept(CallerIdPlaceVisitor<T> visitor);
}


