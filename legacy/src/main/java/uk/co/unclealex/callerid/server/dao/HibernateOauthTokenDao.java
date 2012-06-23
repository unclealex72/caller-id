package uk.co.unclealex.callerid.server.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.OauthToken;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateOauthTokenDao extends HibernateKeyedDao<OauthToken> implements OauthTokenDao {

	@Override
	public OauthToken createExampleBean() {
		return OauthToken.example();
	}

}
