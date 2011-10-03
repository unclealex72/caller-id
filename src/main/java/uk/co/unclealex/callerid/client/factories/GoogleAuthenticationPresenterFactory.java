package uk.co.unclealex.callerid.client.factories;

import uk.co.unclealex.callerid.client.presenters.GoogleAuthenticationPresenter;

public interface GoogleAuthenticationPresenterFactory {

	public GoogleAuthenticationPresenter createGoogleAuthenticationPresenter(String username);
}
