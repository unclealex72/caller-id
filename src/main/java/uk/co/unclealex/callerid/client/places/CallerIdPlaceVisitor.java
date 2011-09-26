package uk.co.unclealex.callerid.client.places;

public interface CallerIdPlaceVisitor<T> {

	T visit(CallerIdPlace callerIdPlace);
	
	T visit(CallListPlace callListPlace);
}
