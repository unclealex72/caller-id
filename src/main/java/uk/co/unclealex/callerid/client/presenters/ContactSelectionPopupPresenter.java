package uk.co.unclealex.callerid.client.presenters;

import java.util.Date;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.factories.ContactSelectionPopupDisplayFactory;
import uk.co.unclealex.callerid.client.presenters.ContactSelectionPopupPresenter.Display;
import uk.co.unclealex.callerid.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.callerid.client.util.ClickHandlerAndFailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.shared.remote.CallerIdServiceAsync;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.assistedinject.Assisted;

public class ContactSelectionPopupPresenter extends AbstractPopupPresenter<DialogBox, Display> implements HasDisplay<Display> {

	public static interface Display extends AbstractPopupPresenter.Display<DialogBox> {
		HasText getContactName();
		HasClickHandlers getSubmitButton();
	}

	private final Display i_display;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final Date i_callRecordTime;
	
	@Inject
	public ContactSelectionPopupPresenter(ContactSelectionPopupDisplayFactory displayFactory,
			AsyncCallbackExecutor asyncCallbackExecutor, @Assisted String[] contactNames, @Assisted Date callRecordTime) {
		super();
		i_display = displayFactory.createContactSelectionPopupDisplay(contactNames);
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_callRecordTime = callRecordTime;
	}

	@Override
	protected void prepare(final Display display) {
		ClickHandler submitClickHandler = 
			new ClickHandlerAndFailureAsPopupExecutableAsyncCallback<Void>(getAsyncCallbackExecutor(), "Updating contact") {
			@Override
			public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<Void> callback) {
				String contactName = display.getContactName().getText().trim();
				if (!contactName.isEmpty()) {
					callerIdService.associateCallRecordToContactName(getCallRecordTime(), contactName, callback);
				}
			}
			public void onSuccess(Void result) {
				hide();
				Window.Location.reload();
			}
		};
		display.getSubmitButton().addClickHandler(submitClickHandler);
	}

	public Display getDisplay() {
		return i_display;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public Date getCallRecordTime() {
		return i_callRecordTime;
	}

}
