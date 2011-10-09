package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.Date;

import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;

public interface ContactService {

	public void updateContacts() throws GoogleAuthenticationFailedException, IOException;

	public void associateCallRecordToContactName(Date callRecordTime, String contactName);

	public void removeContact(String name);

}
