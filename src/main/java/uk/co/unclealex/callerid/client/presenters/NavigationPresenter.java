package uk.co.unclealex.callerid.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.places.CallListPlace;
import uk.co.unclealex.callerid.client.places.CallerIdPlace;
import uk.co.unclealex.callerid.client.places.ContactsPlace;
import uk.co.unclealex.callerid.client.presenters.NavigationPresenter.Display;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class NavigationPresenter implements HasDisplay<Display> {

	public static interface Display extends IsWidget {
		HasClickHandlers getContactsLink();
		HasClickHandlers getCallsLink();
	}

	private final Display i_display;
	private final PlaceController i_placeController;
	
	@Inject
	public NavigationPresenter(Display display, PlaceController placeController) {
		super();
		i_display = display;
		i_placeController = placeController;
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
	
	public Display getDisplay() {
		return i_display;
	}
	
	public PlaceController getPlaceController() {
		return i_placeController;
	}
}
