package uk.co.unclealex.callerid.server.dao;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateTelephoneNumberDao extends HibernateKeyedDao<TelephoneNumber> implements TelephoneNumberDao {

	@Override
	public TelephoneNumber findByNumber(String number) {
		Query query = getSession().createQuery("from TelephoneNumber where number = :number").setString("number", number);
		return uniqueResult(query);
	}
	
	@Override
	public TelephoneNumber createExampleBean() {
		return TelephoneNumber.example();
	}

}
