package uk.co.unclealex.callerid.server.dao;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateUserDao extends HibernateKeyedDao<User> implements UserDao {

	@Override
	public User getUserByName(String username) {
		User user = createExampleBean();
		user.setUsername(username);
		return findByExample(user);
	}
	
	@Override
	public SortedSet<String> getAllUsernames() {
		return asSortedSet(getSession().createQuery("select username from User"), String.class);
	}
	
	@Override
	public User createExampleBean() {
		return User.example();
	}

}
