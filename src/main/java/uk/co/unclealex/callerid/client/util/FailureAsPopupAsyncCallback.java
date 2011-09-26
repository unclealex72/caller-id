package uk.co.unclealex.callerid.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class FailureAsPopupAsyncCallback<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable cause) {
		Window.alert("An unexpected error occurred: " + cause.getMessage());
	}

}
