package uk.co.unclealex.callerid.google.dao;

import uk.co.unclealex.callerid.google.model.User;
import uk.co.unclealex.persistence.dao.BasicDao;

/**
 * A DAO for working with Google users.
 * @author alex
 *
 */
public interface UserDao extends BasicDao<User> {

  /**
   * Find a user by their username (i.e. their gmail email address)
   * @param username The username to look for.
   * @return The user with the given username or null if no such user exists.
   */
  public User findByUsername(String username);
}
