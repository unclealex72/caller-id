package uk.co.unclealex.callerid.server.dao;

import java.util.SortedSet;

import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.hibernate.dao.KeyedDao;

public interface UserDao extends KeyedDao<User> {

	public User getUserByName(String username);

	public SortedSet<String> getAllUsernames();

}
