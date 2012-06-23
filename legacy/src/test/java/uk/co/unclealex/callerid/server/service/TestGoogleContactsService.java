package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.dao.UserDao;
import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.callerid.server.model.GoogleContact;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Transactional
public class TestGoogleContactsService implements GoogleContactsService {

	private UserDao i_userDao;
	
	@Override
	public Map<User, List<GoogleContact>> getAllContactsByUser() throws GoogleAuthenticationFailedException, IOException {
		User userOne = findOrCreateUser("One");
		User userTwo = findOrCreateUser("Two");
		Map<User, List<GoogleContact>> allContactsByUser = Maps.newTreeMap();
		allContactsByUser.put(
			userOne,
			Lists.newArrayList(
				new GoogleContact("Aaron", "01256460837"),
				new GoogleContact("Aaron", "01256732827"),
				new GoogleContact("Brian", "01256460837"),
				new GoogleContact("Carol", "0800123456")));
		allContactsByUser.put(
				userTwo,
				Lists.newArrayList(
					new GoogleContact("Aaron", "01256460837"),
					new GoogleContact("Brian", "0208123456"),
					new GoogleContact("Darren", "0800123456")));
		return allContactsByUser;
	}

	protected User findOrCreateUser(String username) {
		UserDao userDao = getUserDao();
		User user = userDao.getUserByName(username);
		if (user == null) {
			user = new User(username);
			user.setContacts(new TreeSet<Contact>());
			userDao.store(user);
		}
		return user;
	}

	@Override
	public String getClientId() {
		return "TEST";
	}

	@Override
	public void installSuccessCode(User user, String successCode) throws IOException, GoogleAuthenticationFailedException {
		// Do nothing
	}

	public UserDao getUserDao() {
		return i_userDao;
	}

	public void setUserDao(UserDao userDao) {
		i_userDao = userDao;
	}

}
