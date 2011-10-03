package uk.co.unclealex.callerid.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.presenters.UserPresenter.Display;
import uk.co.unclealex.callerid.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.callerid.client.util.ClickHandlerAndFailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.shared.remote.CallerIdServiceAsync;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

public class UserPresenter implements HasDisplay<Display> {

	public static interface Display extends IsWidget {
		HasText getUsername();
		HasClickHandlers getRemoveUserButton();
	}
	
	private final String i_username;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final Display i_display;
	private final UsersPresenter i_usersPresenter;
	
	@Inject
	public UserPresenter(@Assisted String username, UsersPresenter usersPresenter, AsyncCallbackExecutor asyncCallbackExecutor, Display display) {
		super();
		i_username = username;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_display = display;
		i_usersPresenter = usersPresenter;
	}

	public void show(HasWidgets parent) {
		final Display display = getDisplay();
		final String username = getUsername();
		display.getUsername().setText(username);
		ClickHandler handler =
			new ClickHandlerAndFailureAsPopupExecutableAsyncCallback<Void>(
				getAsyncCallbackExecutor(), "Removing user " + username) {

			@Override
			public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<Void> callback) {
				callerIdService.removeUser(username, callback);
			}
			@Override
			public void onSuccess(Void result) {
				getUsersPresenter().refreshUsernames();
			}
		};
		display.getRemoveUserButton().addClickHandler(handler);
		parent.add(display.asWidget());
	}
	
	public String getUsername() {
		return i_username;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public Display getDisplay() {
		return i_display;
	}

	public UsersPresenter getUsersPresenter() {
		return i_usersPresenter;
	}

	
}
