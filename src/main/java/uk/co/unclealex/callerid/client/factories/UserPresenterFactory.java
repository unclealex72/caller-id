package uk.co.unclealex.callerid.client.factories;

import uk.co.unclealex.callerid.client.presenters.UserPresenter;

public interface UserPresenterFactory {

	public UserPresenter createUserPresenter(String username);
}
