package uk.co.unclealex.callerid.remote.dao

import uk.co.unclealex.persistence.dao.BasicDao
import uk.co.unclealex.callerid.remote.model.User

/**
 * An interface for classes that can persist {@link User}s.
 */
interface UserDao extends BasicDao<User> {
    
}