package uk.co.unclealex.callerid.client.presenters;

import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.factories.GoogleAuthenticationPresenterFactory;
import uk.co.unclealex.callerid.client.factories.UserPresenterFactory;
import uk.co.unclealex.callerid.client.presenters.UsersPresenter.Display;
import uk.co.unclealex.callerid.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.callerid.client.util.ClickHandlerAndFailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.callerid.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.shared.remote.CallerIdServiceAsync;

import com.google.common.collect.Sets;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class UsersPresenter extends AbstractActivity implements HasDisplay<Display> {

	public static interface Display extends IsWidget {	
		HasWidgets getExistingUsersPanel();
		HasValue<String> getNewUsername();
		HasClickHandlers getAddNewUserButton();
		HasClickHandlers getUpdateContactsButton();
	}
	
	private final GoogleAuthenticationPresenterFactory i_googleAuthenticationPresenterFactory;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final Display i_display;
	private final UserPresenterFactory i_userPresenterFactory;
	private Set<String> i_usernames = Sets.newHashSet();
	
	@Inject
	public UsersPresenter(
			GoogleAuthenticationPresenterFactory googleAuthenticationPresenterFactory, 
			AsyncCallbackExecutor asyncCallbackExecutor,
			Display display, UserPresenterFactory userPresenterFactory) {
		super();
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_googleAuthenticationPresenterFactory = googleAuthenticationPresenterFactory;
		i_userPresenterFactory = userPresenterFactory;
		i_display = display;
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		final Display display = getDisplay();
		ClickHandler newUserHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String username = display.getNewUsername().getValue().trim();
				if (!username.isEmpty() && !getUsernames().contains(username));
				GoogleAuthenticationPresenter googleAuthenticationPresenter =
					getGoogleAuthenticationPresenterFactory().createGoogleAuthenticationPresenter(username);
				googleAuthenticationPresenter.center();
			}
		};
		display.getAddNewUserButton().addClickHandler(newUserHandler);
		ClickHandler updateContactsHandler = new ClickHandlerAndFailureAsPopupExecutableAsyncCallback<Void>(getAsyncCallbackExecutor(), "Updating Contacts") {
			@Override
			public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<Void> callback) {
				callerIdService.updateContacts(callback);
			}
		};
		display.getUpdateContactsButton().addClickHandler(updateContactsHandler);
		refreshUsernames();
		panel.setWidget(display);
	}


	public void refreshUsernames() {
		ExecutableAsyncCallback<String[]> callback = new FailureAsPopupExecutableAsyncCallback<String[]>() {
			@Override
			public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<String[]> callback) {
				callerIdService.getAllUsernames(callback);
			}
			@Override
			public void onSuccess(String[] usernames) {
				setUsernames(Sets.newTreeSet(Arrays.asList(usernames)));
				final Display display = getDisplay();
				HasWidgets existingUsersPanel = display.getExistingUsersPanel();
				existingUsersPanel.clear();
				UserPresenterFactory userPresenterFactory = getUserPresenterFactory();
				for (String username : usernames) {
					userPresenterFactory.createUserPresenter(username).show(existingUsersPanel);
				}
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}

	public Display getDisplay() {
		return i_display;
	}

	public UserPresenterFactory getUserPresenterFactory() {
		return i_userPresenterFactory;
	}

	public GoogleAuthenticationPresenterFactory getGoogleAuthenticationPresenterFactory() {
		return i_googleAuthenticationPresenterFactory;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public Set<String> getUsernames() {
		return i_usernames;
	}

	public void setUsernames(Set<String> usernames) {
		i_usernames = usernames;
	}

}
