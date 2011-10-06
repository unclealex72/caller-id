package uk.co.unclealex.callerid.server.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

import com.google.common.collect.Lists;

@Transactional
public class HibernateContactDao extends HibernateKeyedDao<Contact> implements ContactDao {

	private TelephoneNumberDao i_telephoneNumberDao;
	
	@Override
	public void removeAll() {
		for (TelephoneNumber telephoneNumber : getTelephoneNumberDao().getAll()) {
			telephoneNumber.getContacts().clear();
		}
		for (Contact contact : Lists.newArrayList(getAll())) {
			remove(contact);
		}
		
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
