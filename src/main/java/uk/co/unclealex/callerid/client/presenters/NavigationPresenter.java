package uk.co.unclealex.callerid.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.places.CallListPlace;
import uk.co.unclealex.callerid.client.places.CallerIdPlace;
import uk.co.unclealex.callerid.client.places.CallerIdPlaceVisitor;
import uk.co.unclealex.callerid.client.places.ContactsPlace;
import uk.co.unclealex.callerid.client.presenters.NavigationPresenter.Display;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class NavigationPresenter implements HasDisplay<Display>, PlaceChangeEvent.Handler, CallerIdPlaceVisitor<HasClickHandlers> {

	public static interface Display extends IsWidget {
		HasClickHandlers getContactsLink();
		HasClickHandlers getCallsLink();
		void select(HasClickHandlers hasClickHandlers);
		void deselect(HasClickHandlers hasClickHandlers);
	}
	
	private final Display i_display;
	private final PlaceController i_placeController;
	
	@Inject
	public NavigationPresenter(Display display, PlaceController placeController, EventBus eventBus) {
		super();
		i_display = display;
		i_placeController = placeController;
		eventBus.addHandler(PlaceChangeEvent.TYPE, this);
	}

	public void show(HasWidgets hasWidgets) {
		Display display = getDisplay();
		display.getContactsLink().addClickHandler(createClickHandler(new ContactsPlace()));
		display.getCallsLink().addClickHandler(createClickHandler(new CallListPlace()));
		hasWidgets.add(display.asWidget());
	}

	protected ClickHandler createClickHandler(final CallerIdPlace callerIdPlace) {
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getPlaceController().goTo(callerIdPlace);
			}
		};
		return handler;
	}
	
	@Override
	public void onPlaceChange(PlaceChangeEvent event) {
		Place newPlace = event.getNewPlace();
		if (newPlace instanceof CallerIdPlace) {
			HasClickHandlers selectedLink = ((CallerIdPlace) newPlace).accept(this);
			Display display = getDisplay();
			for (
				HasClickHandlers hasClickHandlers : 
				new HasClickHandlers[] { display.getContactsLink(), display.getCallsLink() }) {
				if (hasClickHandlers.equals(selectedLink)) {
					display.select(hasClickHandlers);
				}
				else {
					display.deselect(hasClickHandlers);
				}
			}
		}
	}
	
	@Override
	public HasClickHandlers visit(CallerIdPlace callerIdPlace) {
		return null;
	}
	
	@Override
	public HasClickHandlers visit(CallListPlace callListPlace) {
		return getDisplay().getCallsLink();
	}
	
	@Override
	public HasClickHandlers visit(ContactsPlace contactsPlace) {
		return getDisplay().getContactsLink();
	}
	
	public Display getDisplay() {
		return i_display;
	}
	
	public PlaceController getPlaceController() {
		return i_placeController;
	}
}
