package uk.co.unclealex.callerid.server.dao;

import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.hibernate.dao.KeyedDao;

public interface TelephoneNumberDao extends KeyedDao<TelephoneNumber> {

	public TelephoneNumber findByNumber(String number);

}
