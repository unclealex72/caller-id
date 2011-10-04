package uk.co.unclealex.callerid.client;

import uk.co.unclealex.callerid.client.gin.CallerIdGinjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class CallerId implements EntryPoint {

	@Override
	public void onModuleLoad() {
		final CallerIdGinjector injector = GWT.create(CallerIdGinjector.class);

		RootPanel.get("welcome").add(injector.getMainPanel());
		injector.getNavigationPresenter().show(RootPanel.get("sidebar"));
		
		injector.getPlaceHistoryHandler().handleCurrentHistory();
	}

}
