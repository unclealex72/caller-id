package uk.co.unclealex.callerid.client;

import uk.co.unclealex.callerid.client.gin.CallerIdGinjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class CallerId implements EntryPoint {

	@Override
	public void onModuleLoad() {
		
		final CallerIdGinjector injector = GWT.create(CallerIdGinjector.class);

		injector.getPlaceHistoryHandler().handleCurrentHistory();
	}

}
