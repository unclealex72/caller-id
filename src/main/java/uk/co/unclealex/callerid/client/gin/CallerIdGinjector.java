package uk.co.unclealex.callerid.client.gin;

import uk.co.unclealex.callerid.client.presenters.NavigationPresenter;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.SimplePanel;

@GinModules({ CallerIdClientModule.class, CallerIdInternalModule.class })
public interface CallerIdGinjector extends Ginjector {

	PlaceHistoryHandler getPlaceHistoryHandler();
	SimplePanel getMainPanel();
	NavigationPresenter getNavigationPresenter();
}
