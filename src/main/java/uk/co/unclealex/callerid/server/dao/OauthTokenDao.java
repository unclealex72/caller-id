package uk.co.unclealex.callerid.server.dao;

import uk.co.unclealex.callerid.server.model.OauthToken;
import uk.co.unclealex.callerid.server.model.OauthTokenType;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.hibernate.dao.KeyedDao;

public interface OauthTokenDao extends KeyedDao<OauthToken> {

	public OauthToken findByUserAndType(User user, OauthTokenType access);

	public void removeByUser(User user);

}
