package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
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
import uk.co.unclealex.callerid.shared.model.PhoneNumber;
import uk.co.unclealex.callerid.shared.remote.CallerIdService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Sets;

@Transactional(rollbackFor={IOException.class, GoogleAuthenticationFailedException.class})
public class CallerIdServiceImpl implements CallerIdService {

	private UserDao i_userDao;
	private OauthTokenDao i_oauthTokenDao;
	private GoogleContactsService i_googleContactsService;
	private ContactDao i_contactDao;
	private TelephoneNumberFactory i_telephoneNumberFactory;
	private TelephoneNumberDao i_telephoneNumberDao;
	private NumberLocationService i_numberLocationService;
	private CallRecordDao i_callRecordDao;
	
	@Override
	public void addUser(String username, String token) throws IOException, GoogleAuthenticationFailedException {
		User user = new User(username);
		getUserDao().store(user);
		getGoogleContactsService().installSuccessCode(user, token);
	}

	@Override
	public void removeUser(String username) {
		User user = getUserDao().getUserByName(username);
		getOauthTokenDao().removeByUser(user);
		getUserDao().remove(user);
	}

	@Override
	public String[] getAllUsernames() {
		return Iterables.toArray(getUserDao().getAllUsernames(), String.class);
	}
	
	@Override
	public void updateContacts() throws GoogleAuthenticationFailedException, IOException {
		ContactDao contactDao = getContactDao();
		TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
		contactDao.removeAll();
		Map<String, Collection<String>> allContactsByTelephoneNumber = getGoogleContactsService().getAllContactsByTelephoneNumber();
		final TelephoneNumberFactory telephoneNumberFactory = getTelephoneNumberFactory();
		EntryTransformer<String, Collection<String>, TelephoneNumber> transformer = 
				new EntryTransformer<String, Collection<String>, TelephoneNumber>() {
			@Override
			public TelephoneNumber transformEntry(String number, Collection<String> value) {
				return telephoneNumberFactory.findOrCreateTelephoneNumber(number);
			}
		};
		Map<String, TelephoneNumber> telephoneNumbers = Maps.transformEntries(allContactsByTelephoneNumber, transformer);
		Map<String, Contact> contactCache = Maps.newHashMap();
		for (Entry<String, Collection<String>> entry : allContactsByTelephoneNumber.entrySet()) {
			TelephoneNumber telephoneNumber = telephoneNumbers.get(entry.getKey());
			SortedSet<Contact> contacts = telephoneNumber.getContacts();
			for (String name : entry.getValue()) {
				Contact contact = contactCache.get(name);
				if (contact == null) {
					contact = new Contact(name);
					contactDao.store(contact);
					contactCache.put(name, contact);
				}
				contacts.add(contact);
			}
			telephoneNumberDao.store(telephoneNumber);
		}
	}

	@Override
	public CallRecord[] getAllCallRecords() {
		final NumberLocationService numberLocationService = getNumberLocationService();
		final Function<Contact, String> nameFunction = new Function<Contact, String>() {
			@Override
			public String apply(Contact contact) {
				return contact.getName();
			}
		};
		Function<uk.co.unclealex.callerid.server.model.CallRecord, CallRecord> callRecordFunction = 
			new Function<uk.co.unclealex.callerid.server.model.CallRecord, CallRecord>() {
			@Override
			public CallRecord apply(uk.co.unclealex.callerid.server.model.CallRecord callRecord) {
				Date callTime = callRecord.getCallDate();
				SortedSet<String> contacts;
				TelephoneNumber telephoneNumber = callRecord.getTelephoneNumber();
				PhoneNumber phoneNumber;
				boolean blocked;
				if (telephoneNumber != null) {
					phoneNumber = numberLocationService.decomposeNumber(telephoneNumber.getNumber());
					blocked = telephoneNumber.isBlocked();
					contacts = Sets.newTreeSet(Iterables.transform(telephoneNumber.getContacts(), nameFunction));
				}
				else {
					phoneNumber = null;
					blocked = false;
					contacts = Sets.newTreeSet();
				}
				return new CallRecord(callTime, phoneNumber, blocked, contacts);
			}
		};
		return Iterables.toArray(
				Sets.newTreeSet(Iterables.transform(getCallRecordDao().getAll(), callRecordFunction)),
				CallRecord.class);
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

	public ContactDao getContactDao() {
		return i_contactDao;
	}

	public void setContactDao(ContactDao contactDao) {
		i_contactDao = contactDao;
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
}
