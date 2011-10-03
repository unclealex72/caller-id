package uk.co.unclealex.callerid.shared.service;

public interface GoogleConstants {

	public String GOOGLE_OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
	public String CONTACTS_FEED = "https://www.google.com/m8/feeds/contacts/default/full";
	public String CONSUMER_KEY = "720703205602.apps.googleusercontent.com";

	public String AUTHENTICATION_URL =
		String.format(
			"%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
			GOOGLE_OAUTH_URL, CONSUMER_KEY, "urn:ietf:wg:oauth:2.0:oob", CONTACTS_FEED);

}
