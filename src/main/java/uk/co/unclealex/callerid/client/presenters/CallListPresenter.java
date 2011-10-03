package uk.co.unclealex.callerid.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.factory.CallPresenterFactory;
import uk.co.unclealex.callerid.client.presenters.CallListPresenter.Display;
import uk.co.unclealex.callerid.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.callerid.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.callerid.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.shared.model.CallRecord;
import uk.co.unclealex.callerid.shared.remote.CallerIdServiceAsync;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class CallListPresenter extends AbstractActivity implements HasDisplay<Display> {

	public static interface Display extends IsWidget {
		HasWidgets getCallRecordPanel();
	}
	
	private final Display i_display;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final CallPresenterFactory i_callPresenterFactory;
	
	@Inject
	public CallListPresenter(Display display, AsyncCallbackExecutor asyncCallbackExecutor, CallPresenterFactory callPresenterFactory) {
		super();
		i_display = display;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_callPresenterFactory = callPresenterFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Display display = getDisplay();
		final HasWidgets callRecordPanel = display.getCallRecordPanel();
		final CallPresenterFactory callPresenterFactory = getCallPresenterFactory();
		ExecutableAsyncCallback<CallRecord[]> callback = new FailureAsPopupExecutableAsyncCallback<CallRecord[]>() {
			@Override
			public void onSuccess(CallRecord[] callRecords) {
				for (CallRecord callRecord : callRecords) {
					callPresenterFactory.createCallPresenter(callRecord).show(callRecordPanel);
				}
			}
			@Override
			public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<CallRecord[]> callback) {
				callerIdService.getAllCallRecords(callback);
			}
		};
		getAsyncCallbackExecutor().execute(callback);
		panel.setWidget(display);
	}

	public Display getDisplay() {
		return i_display;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public CallPresenterFactory getCallPresenterFactory() {
		return i_callPresenterFactory;
	}
}
