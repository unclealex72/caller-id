package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.dao.CallRecordDao;
import uk.co.unclealex.callerid.server.dao.ContactDao;
import uk.co.unclealex.callerid.server.dao.OauthTokenDao;
import uk.co.unclealex.callerid.server.dao.TelephoneNumberDao;
import uk.co.unclealex.callerid.server.dao.UserDao;
import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.callerid.shared.model.CallRecord;
import uk.co.unclealex.callerid.shared.model.CallRecordContact;
import uk.co.unclealex.callerid.shared.model.CallRecords;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;
import uk.co.unclealex.callerid.shared.remote.CallerIdService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Transactional(rollbackFor={IOException.class, GoogleAuthenticationFailedException.class})
public class CallerIdServiceImpl implements CallerIdService {

	private UserDao i_userDao;
	private OauthTokenDao i_oauthTokenDao;
	private GoogleContactsService i_googleContactsService;
	private TelephoneNumberFactory i_telephoneNumberFactory;
	private TelephoneNumberDao i_telephoneNumberDao;
	private NumberLocationService i_numberLocationService;
	private CallRecordDao i_callRecordDao;
	private ContactService i_contactService;
	private ContactDao i_contactDao;
	
	@Override
	public void addUser(String username, String token) throws IOException, GoogleAuthenticationFailedException {
		User user = new User(username);
		getUserDao().store(user);
		getGoogleContactsService().installSuccessCode(user, token);
	}

	@Override
	public String[] getAllContactNames() {
		return Iterables.toArray(getContactDao().getAllContactNames(), String.class);
	}
	
	@Override
	public void associateCallRecordToContactName(Date callRecordTime, String contactName) {
		getContactService().associateCallRecordToContactName(callRecordTime, contactName);
	}
	
	@Override
	public void removeUser(String username) {
		User user = getUserDao().getUserByName(username);
		getUserDao().remove(user);
	}

	@Override
	public String[] getAllUsernames() {
		return Iterables.toArray(getUserDao().getAllUsernames(), String.class);
	}
	
	@Override
	public void updateContacts() throws GoogleAuthenticationFailedException, IOException {
		getContactService().updateContacts();
	}

	@Override
	public CallRecords getAllCallRecords(int page, int callsPerPage) {
		final NumberLocationService numberLocationService = getNumberLocationService();
		final Function<Contact, CallRecordContact> contactFunction = new Function<Contact, CallRecordContact>() {
			@Override
			public CallRecordContact apply(Contact contact) {
				return new CallRecordContact(contact.getName(), !contact.getUsers().isEmpty());
			}
		};
		Function<uk.co.unclealex.callerid.server.model.CallRecord, CallRecord> callRecordFunction = 
			new Function<uk.co.unclealex.callerid.server.model.CallRecord, CallRecord>() {
			@Override
			public CallRecord apply(uk.co.unclealex.callerid.server.model.CallRecord callRecord) {
				Date callTime = callRecord.getCallDate();
				SortedSet<CallRecordContact> contacts;
				TelephoneNumber telephoneNumber = callRecord.getTelephoneNumber();
				PhoneNumber phoneNumber;
				boolean blocked;
				if (telephoneNumber != null) {
					phoneNumber = numberLocationService.decomposeNumber(telephoneNumber.getNumber());
					blocked = telephoneNumber.isBlocked();
					contacts = Sets.newTreeSet(Iterables.transform(telephoneNumber.getContacts(), contactFunction));
				}
				else {
					phoneNumber = null;
					blocked = false;
					contacts = Sets.newTreeSet();
				}
				return new CallRecord(callTime, phoneNumber, blocked, contacts);
			}
		};
		
		CallRecordDao callRecordDao = getCallRecordDao();
		List<CallRecord> callRecords = 
			Lists.newArrayList(Iterables.transform(callRecordDao.getCallRecords(page, callsPerPage), callRecordFunction));
		int callRecordCount = (int) callRecordDao.count();
		int pageCount = 1 + (callRecordCount / callsPerPage);
		return new CallRecords(callRecords, pageCount, callRecordCount);
	}
	
	@Override
	public void removeContact(String name) {
		getContactService().removeContact(name);
	}
	
	public UserDao getUserDao() {
		return i_userDao;
	}

	public void setUserDao(UserDao userDao) {
		i_userDao = userDao;
	}

	public OauthTokenDao getOauthTokenDao() {
		return i_oauthTokenDao;
	}

	public void setOauthTokenDao(OauthTokenDao oauthTokenDao) {
		i_oauthTokenDao = oauthTokenDao;
	}

	public GoogleContactsService getGoogleContactsService() {
		return i_googleContactsService;
	}

	public void setGoogleContactsService(GoogleContactsService googleContactsService) {
		i_googleContactsService = googleContactsService;
	}

	public TelephoneNumberFactory getTelephoneNumberFactory() {
		return i_telephoneNumberFactory;
	}

	public void setTelephoneNumberFactory(TelephoneNumberFactory telephoneNumberFactory) {
		i_telephoneNumberFactory = telephoneNumberFactory;
	}

	public TelephoneNumberDao getTelephoneNumberDao() {
		return i_telephoneNumberDao;
	}

	public void setTelephoneNumberDao(TelephoneNumberDao telephoneNumberDao) {
		i_telephoneNumberDao = telephoneNumberDao;
	}

	public NumberLocationService getNumberLocationService() {
		return i_numberLocationService;
	}

	public void setNumberLocationService(NumberLocationService numberLocationService) {
		i_numberLocationService = numberLocationService;
	}

	public void setCallRecordDao(CallRecordDao callRecordDao) {
		i_callRecordDao = callRecordDao;
	}
	
	public CallRecordDao getCallRecordDao() {
		return i_callRecordDao;
	}

	public ContactService getContactService() {
		return i_contactService;
	}

	public void setContactService(ContactService contactService) {
		i_contactService = contactService;
	}

	public ContactDao getContactDao() {
		return i_contactDao;
	}

	public void setContactDao(ContactDao contactDao) {
		i_contactDao = contactDao;
	}
}
