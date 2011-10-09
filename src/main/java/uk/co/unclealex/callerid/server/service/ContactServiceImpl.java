package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.dao.CallRecordDao;
import uk.co.unclealex.callerid.server.dao.ContactDao;
import uk.co.unclealex.callerid.server.dao.TelephoneNumberDao;
import uk.co.unclealex.callerid.server.dao.UserDao;
import uk.co.unclealex.callerid.server.model.CallRecord;
import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.callerid.server.model.GoogleContact;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.common.collect.Sets;

@Transactional
public class ContactServiceImpl implements ContactService {

	private static final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);
	
	private GoogleContactsService i_googleContactsService;
	private UserDao i_userDao;
	private ContactDao i_contactDao;
	private TelephoneNumberFactory i_telephoneNumberFactory;
	private TelephoneNumberDao i_telephoneNumberDao;
	private CallRecordDao i_callRecordDao;

	@Override
	public void updateContacts() throws GoogleAuthenticationFailedException, IOException {
		ContactDao contactDao = getContactDao();
		TelephoneNumberFactory telephoneNumberFactory = getTelephoneNumberFactory();
		UserDao userDao = getUserDao();
		TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
		for (User user : userDao.getAll()) {
			for (Contact contact : Sets.newTreeSet(user.getContacts())) {
				log.info("Removing contact " + contact.getName());
				for (TelephoneNumber telephoneNumber : contact.getTelephoneNumbers()) {
					telephoneNumber.getContacts().remove(contact);
				}
				for (User contactUser : contact.getUsers()) {
					contactUser.getContacts().remove(contact);
				}
				contactDao.remove(contact);
			}
		}
		Map<User, List<GoogleContact>> allContactsByUser = getGoogleContactsService().getAllContactsByUser();
		for (Entry<User, List<GoogleContact>> entry : allContactsByUser.entrySet()) {
			User user = entry.getKey();
			SortedSet<Contact> contacts = user.getContacts();
			for (GoogleContact googleContact : entry.getValue()) {
				String name = googleContact.getName();
				String number = googleContact.getTelephoneNumber();
				log.info(String.format("Storing contact %s with phone number %s for user %s", name, number, user.getUsername()));
				Contact contact = findOrCreateContact(name);
				TelephoneNumber telephoneNumber = 
					telephoneNumberFactory.findOrCreateTelephoneNumber(number);
				telephoneNumber.getContacts().add(contact);
				contacts.add(contact);
				contact.getTelephoneNumbers().add(telephoneNumber);
				contact.getUsers().add(user);
				contactDao.store(contact);
				userDao.store(user);
				telephoneNumberDao.store(telephoneNumber);
			}
		}
	}

	protected Contact findOrCreateContact(String name) {
		ContactDao contactDao = getContactDao();
		Contact contact = contactDao.findByName(name);
		if (contact == null) {
			contact = new Contact(name);
			contact.setTelephoneNumbers(new TreeSet<TelephoneNumber>());
			contact.setUsers(new TreeSet<User>());
			contactDao.store(contact);
		}
		return contact;
	}

	@Override
	public void associateCallRecordToContactName(Date callRecordTime, String contactName) {
		CallRecord callRecord = getCallRecordDao().findByTime(callRecordTime);
		Contact contact = findOrCreateContact(contactName);
		TelephoneNumber telephoneNumber = callRecord.getTelephoneNumber();
		telephoneNumber.getContacts().add(contact);
		contact.getTelephoneNumbers().add(telephoneNumber);
		getContactDao().store(contact);
		getTelephoneNumberDao().store(telephoneNumber);
	}
	
	@Override
	public void removeContact(String name) {
		Contact contact = getContactDao().findByName(name);
		if (contact != null) {
			log.info("Removing contact " + name);
			TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
			UserDao userDao = getUserDao();
			SortedSet<TelephoneNumber> telephoneNumbers = contact.getTelephoneNumbers();
			for (TelephoneNumber telephoneNumber : Sets.newTreeSet(telephoneNumbers)) {
				telephoneNumber.getContacts().remove(contact);
				telephoneNumberDao.store(telephoneNumber);
			}
			SortedSet<User> users = contact.getUsers();
			for (User user : Sets.newTreeSet(users)) {
				user.getContacts().remove(contact);
				userDao.store(user);
			}
			telephoneNumbers.clear();
			users.clear();
			getContactDao().remove(contact);
		}
	}
	
	public GoogleContactsService getGoogleContactsService() {
		return i_googleContactsService;
	}

	public void setGoogleContactsService(GoogleContactsService googleContactsService) {
		i_googleContactsService = googleContactsService;
	}

	public UserDao getUserDao() {
		return i_userDao;
	}

	public void setUserDao(UserDao userDao) {
		i_userDao = userDao;
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

	public CallRecordDao getCallRecordDao() {
		return i_callRecordDao;
	}

	public void setCallRecordDao(CallRecordDao callRecordDao) {
		i_callRecordDao = callRecordDao;
	}

}
