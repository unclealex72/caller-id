package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;

public interface GoogleContactsService {

	public Map<String, Collection<String>> getAllContactsByTelephoneNumber() throws GoogleAuthenticationFailedException, IOException;

	public String getClientId();

	public void installSuccessCode(User user, String successCode) throws IOException, GoogleAuthenticationFailedException;

}
