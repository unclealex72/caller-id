package uk.co.unclealex.callerid.google.dao;

import uk.co.unclealex.callerid.google.model.User;
import uk.co.unclealex.callerid.persistence.BasicDao;

/**
 * A DAO for working with Google users.
 * @author alex
 *
 */
public interface UserDao extends BasicDao<User> {

  public User findByUsername(String username);
}
