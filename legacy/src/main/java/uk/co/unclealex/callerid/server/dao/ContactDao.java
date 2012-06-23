package uk.co.unclealex.callerid.server.dao;

import java.util.SortedSet;

import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.hibernate.dao.KeyedDao;

public interface ContactDao extends KeyedDao<Contact> {

	public Contact findByName(String name);

	public SortedSet<String> getAllContactNames();
}
