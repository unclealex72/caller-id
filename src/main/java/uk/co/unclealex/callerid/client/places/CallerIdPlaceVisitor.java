package uk.co.unclealex.callerid.client.places;


public interface CallerIdPlaceVisitor<T> {

	T visit(CallListPlace callListPlace);

	T visit(ContactsPlace contactsPlace);

	T visit(CallerIdPlace callerIdPlace);
}
