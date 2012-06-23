package uk.co.unclealex.callerid.server.dao;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateContactDao extends HibernateKeyedDao<Contact> implements ContactDao {

	private TelephoneNumberDao i_telephoneNumberDao;

	@Override
	public Contact findByName(String name) {
		Contact contact = createExampleBean();
		contact.setName(name);
		return findByExample(contact);
	}
	
	@Override
	public SortedSet<String> getAllContactNames() {
		return asSortedSet(getSession().createQuery("select name from Contact"), String.class);
	}
	
	@Override
	public Contact createExampleBean() {
		return Contact.example();
	}

	public TelephoneNumberDao getTelephoneNumberDao() {
		return i_telephoneNumberDao;
	}

	public void setTelephoneNumberDao(TelephoneNumberDao telephoneNumberDao) {
		i_telephoneNumberDao = telephoneNumberDao;
	}

}
