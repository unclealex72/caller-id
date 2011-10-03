package uk.co.unclealex.callerid.server.dao;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.OauthToken;
import uk.co.unclealex.callerid.server.model.OauthTokenType;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateOauthTokenDao extends HibernateKeyedDao<OauthToken> implements OauthTokenDao {

	@Override
	public OauthToken findByUserAndType(User user, OauthTokenType oauthTokenType) {
		OauthToken oauthToken = createExampleBean();
		oauthToken.setUser(user);
		oauthToken.setTokenType(oauthTokenType);
		return findByExample(oauthToken);
	}
	
	@Override
	public void removeByUser(User user) {
		Query query = getSession().createQuery("delete from OauthToken where user = :user").setEntity("user", user);
		query.executeUpdate();
	}
	
	@Override
	public OauthToken createExampleBean() {
		return OauthToken.example();
	}

}
