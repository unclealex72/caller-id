package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.dao.ContactDao;
import uk.co.unclealex.callerid.server.dao.TelephoneNumberDao;
import uk.co.unclealex.callerid.server.dao.UserDao;
import uk.co.unclealex.callerid.server.model.BusinessKeyedBean;
import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
	locations = { "classpath:applicationContext-callerid.xml", "classpath:applicationContext-callerid-test.xml" })
@TransactionConfiguration
@Transactional
public class ContactServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired ContactService contactService;
	@Autowired UserDao userDao;
	@Autowired ContactDao contactDao;
	@Autowired TelephoneNumberDao telephoneNumberDao;
	
	@Test
	public void testContacts() throws GoogleAuthenticationFailedException, IOException {
		doTestContacts();
		doTestContacts();
	}

	protected void doTestContacts() 
					throws GoogleAuthenticationFailedException, IOException {
		contactService.updateContacts();
		testUser(userDao, "One", "Aaron", "Brian", "Carol");
		testUser(userDao, "Two", "Aaron", "Brian", "Darren");
		testContactUser(contactDao, "Aaron", "One", "Two");
		testContactUser(contactDao, "Brian", "One", "Two");
		testContactUser(contactDao, "Carol", "One");
		testContactUser(contactDao, "Darren", "Two");
		testContactTelephoneNumber(contactDao, "Aaron", "+441256460837", "+441256732827");
		testContactTelephoneNumber(contactDao, "Brian", "+441256460837", "+44208123456");
		testContactTelephoneNumber(contactDao, "Carol", "+44800123456");
		testContactTelephoneNumber(contactDao, "Darren", "+44800123456");
		testTelephoneNumber(telephoneNumberDao, "+441256460837", "Aaron", "Brian");
		testTelephoneNumber(telephoneNumberDao, "+441256732827", "Aaron");
		testTelephoneNumber(telephoneNumberDao, "+44800123456", "Carol", "Darren");
		testTelephoneNumber(telephoneNumberDao, "+44208123456", "Brian");
	}
	
	protected void testUser(final UserDao userDao, String username, String... contactNames) {
		Function<String, User> userFactory = new Function<String, User>() {
			@Override
			public User apply(String username) {
				return userDao.getUserByName(username);
			}
		};
		Function<User, SortedSet<Contact>> contactFactory = new Function<User, SortedSet<Contact>>() {
			@Override
			public SortedSet<Contact> apply(User user) {
				return user.getContacts();
			}
		};
		testResults(userFactory, contactFactory, "user", username, "contact", contactNames);
	}

	protected void testContactUser(ContactDao contactDao, String contactName, String... usernames) {
		Function<Contact, SortedSet<User>> userFactory = new Function<Contact, SortedSet<User>>() {
			public SortedSet<User> apply(Contact contact) {
				return contact.getUsers();
			}
		};
		testContact(contactDao, contactName, userFactory, "user", usernames);
	}
	
	protected void testContactTelephoneNumber(ContactDao contactDao, String contactName, String... telephoneNumbers) {
		Function<Contact, SortedSet<TelephoneNumber>> userFactory = new Function<Contact, SortedSet<TelephoneNumber>>() {
			public SortedSet<TelephoneNumber> apply(Contact contact) {
				return contact.getTelephoneNumbers();
			}
		};
		testContact(contactDao, contactName, userFactory, "telephone number", telephoneNumbers);
	}

	protected <C extends BusinessKeyedBean<C, String>> void testContact(
			final ContactDao contactDao, String contactName, 
			Function<Contact, SortedSet<C>> childFactory, String childType, String... childKeys) {
		Function<String, Contact> contactFactory = new Function<String, Contact>() {
			@Override
			public Contact apply(String contactName) {
				return contactDao.findByName(contactName);
			}
		};
		testResults(contactFactory, childFactory, "contact", contactName, childType, childKeys);
	}
	
	protected void testTelephoneNumber(final TelephoneNumberDao telephoneNumberDao, String telephoneNumber, String... contactNames) {
		Function<String, TelephoneNumber> telephoneNumberFactory = new Function<String, TelephoneNumber>() {
			public TelephoneNumber apply(String telephoneNumber) {
				return telephoneNumberDao.findByNumber(telephoneNumber);
			}
		};
		Function<TelephoneNumber, SortedSet<Contact>> contactFactory = new Function<TelephoneNumber, SortedSet<Contact>>() {
			@Override
			public SortedSet<Contact> apply(TelephoneNumber telephoneNumber) {
				return telephoneNumber.getContacts();
			}
		};
		testResults(telephoneNumberFactory, contactFactory, "telephone number", telephoneNumber, "contact", contactNames);
	}
	
	protected <I extends BusinessKeyedBean<I, String>, C extends BusinessKeyedBean<C, String>> void testResults(
		Function<String, I> itemFactory, Function<I, SortedSet<C>> childFactory, 
		String itemType, String itemKey, String childType, String... childKeys) {
		I item = itemFactory.apply(itemKey);
		Assert.assertNotNull("Could not find the " + itemType + " identified by " + itemKey, item);
		SortedSet<C> children = childFactory.apply(item);
		Assert.assertNotNull(
				"Could not find any childen of type " + childType + " for " + itemType + " identified by " + itemKey, children);
		Function<C, String> keyFunction = new Function<C, String>() {
			public String apply(C child) {
				return child.getBusinessKey();
			}
		};
		Assert.assertArrayEquals(
			itemType + " " + itemKey + " had the wrong children of type " + childType, 
			Iterables.toArray(Sets.newTreeSet(Arrays.asList(childKeys)), String.class), 
			Iterables.toArray(Iterables.transform(children, keyFunction), String.class));
	}
}
