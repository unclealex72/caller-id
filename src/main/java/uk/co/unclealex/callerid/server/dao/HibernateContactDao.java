package uk.co.unclealex.callerid.server.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateContactDao extends HibernateKeyedDao<Contact> implements ContactDao {

	@Override
	public void removeAll() {
		getSession().createQuery("delete from Contact").executeUpdate();
	}
	
	@Override
	public Contact createExampleBean() {
		return Contact.example();
	}

}
