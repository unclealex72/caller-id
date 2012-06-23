package uk.co.unclealex.callerid.shared.service;

public interface Constants {

	public String GOOGLE_OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
	public String CONTACTS_FEED = "https://www.google.com/m8/feeds/contacts/default/full";
	public String CONSUMER_KEY = "720703205602.apps.googleusercontent.com";

	public String AUTHENTICATION_URL =
			GOOGLE_OAUTH_URL + "?client_id=" + CONSUMER_KEY + 
			"&redirect_uri=urn:ietf:wg:oauth:2.0:oob&scope=" + CONTACTS_FEED +"&response_type=code";
	public String BASINGSTOKE = "1256";
	public String UK = "44";

}
