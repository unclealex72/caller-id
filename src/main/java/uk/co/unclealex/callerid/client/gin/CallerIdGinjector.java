package uk.co.unclealex.callerid.client.gin;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceHistoryHandler;

@GinModules({ CallerIdClientModule.class, CallerIdInternalModule.class })
public interface CallerIdGinjector extends Ginjector {

	PlaceHistoryHandler getPlaceHistoryHandler();
	ActivityManager getActivityMapper();
}
