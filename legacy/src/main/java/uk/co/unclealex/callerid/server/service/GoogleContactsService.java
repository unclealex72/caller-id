package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.co.unclealex.callerid.server.model.GoogleContact;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;

public interface GoogleContactsService {

	public Map<User, List<GoogleContact>> getAllContactsByUser() throws GoogleAuthenticationFailedException, IOException;

	public String getClientId();

	public void installSuccessCode(User user, String successCode) throws IOException, GoogleAuthenticationFailedException;

}
