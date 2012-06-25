package uk.co.unclealex.callerid.shared.remote;

import java.io.IOException;
import java.util.Date;

import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.callerid.shared.model.CallRecords;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("callerid")
public interface CallerIdService extends RemoteService {

	public void addUser(String username, String token) throws IOException, GoogleAuthenticationFailedException;
	public void removeUser(String username);
	public String[] getAllUsernames();
	public String[] getAllContactNames();
	public void associateCallRecordToContactName(Date callRecordTime, String contactName);
	public CallRecords getAllCallRecords(int page, int callsPerPage);
	
	public void removeContact(String name);
	public void updateContacts() throws GoogleAuthenticationFailedException, IOException;
}