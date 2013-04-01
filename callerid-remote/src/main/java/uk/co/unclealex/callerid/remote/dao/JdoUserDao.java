package uk.co.unclealex.callerid.remote.dao;

import javax.jdo.PersistenceManagerFactory;

import uk.co.unclealex.callerid.remote.model.QUser;
import uk.co.unclealex.callerid.remote.model.User;
import uk.co.unclealex.persistence.jdo.JdoBasicDao;
import uk.co.unclealex.persistence.paging.PagingService;

/**
 * The JDO implementation of {@link UserDao}.
 */
public class JdoUserDao extends JdoBasicDao<User, QUser> implements UserDao {

  public JdoUserDao(final PersistenceManagerFactory persistenceManagerFactory, final PagingService pagingService) {
    super(User.class, persistenceManagerFactory, pagingService);
  }

  @Override
  public QUser candidate() {
    return QUser.candidate();
  }

}