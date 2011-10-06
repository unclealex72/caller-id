/**
 * Copyright 2010 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */
package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.dao.OauthTokenDao;
import uk.co.unclealex.callerid.server.dao.UserDao;
import uk.co.unclealex.callerid.server.model.OauthToken;
import uk.co.unclealex.callerid.server.model.OauthTokenType;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.callerid.shared.service.Constants;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Transactional
public class GoogleContactsServiceImpl implements GoogleContactsService {

	private static final String GOOGLE_OAUTH_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";	
	private static final String CONSUMER_SECRET = "ArBPhkHBOOPSaaj7wZ-KFXNu";

	//private static final Logger log = LoggerFactory.getLogger(GoogleContactsServiceImpl.class);
	
	private static final long EXPIRY_LEEWAY = 1000 * 60 * 10; // 10 Minutes
	
	private UserDao i_userDao;
	private OauthTokenDao i_oauthTokenDao;
	
	@Override
	public Map<String, Collection<String>> getAllContactsByTelephoneNumber() throws GoogleAuthenticationFailedException, IOException {
		Supplier<Set<String>> factory = new Supplier<Set<String>>() {
			@Override
			public Set<String> get() {
				return Sets.newHashSet();
			}
		};
		Map<String, Collection<String>> map = Maps.newHashMap();
		Multimap<String, String> multimap = Multimaps.newSetMultimap(map, factory);
		for (User user : getUserDao().getAll()) {
			try {
				addAllContactsByTelephoneNumber(multimap, user);
			}
			catch (ServiceException e) {
				throw new IOException(e);
			}
		}
		return map;
	}

	protected void addAllContactsByTelephoneNumber(Multimap<String, String> allContactsByTelephoneNumber, User user) throws GoogleAuthenticationFailedException, IOException, ServiceException {
		ContactsService contactsService = createContactsService(user);
	  ContactFeed resultFeed = contactsService.getFeed(new URL(Constants.CONTACTS_FEED), ContactFeed.class);
	  for (ContactEntry entry : resultFeed.getEntries()) {
	  	if (entry.hasName()) {
	  		String name = entry.getName().getFullName().getValue();
	  		for (PhoneNumber phoneNumber : entry.getPhoneNumbers()) {
	  			String number = phoneNumber.getPhoneNumber();
	  			allContactsByTelephoneNumber.put(number, name);
	  		}
	  	}
	  }
	}
	protected ContactsService createContactsService(User user) throws GoogleAuthenticationFailedException, IOException {
		return new ContactsService("callerid.unclealex.co.uk", getOauthToken(user));
	}
	
	protected String getOauthToken(User user) throws GoogleAuthenticationFailedException, IOException {
		OauthTokenDao oauthTokenDao = getOauthTokenDao();
		OauthToken accessToken = oauthTokenDao.findByUserAndType(user, OauthTokenType.ACCESS);
		if (accessToken == null || accessToken.getExpiryDate().getTime() - EXPIRY_LEEWAY < System.currentTimeMillis()) {
			if (accessToken == null) {
				accessToken = new OauthToken(OauthTokenType.ACCESS, user);
				accessToken.setTokenType(OauthTokenType.ACCESS);
				accessToken.setUser(user);
			}
			updateAccessToken(user, accessToken);
			oauthTokenDao.store(accessToken);
		}
		return accessToken.getToken();
	}

	@Override
	public void installSuccessCode(final User user, String successCode) throws IOException, GoogleAuthenticationFailedException {
		final OauthTokenDao oauthTokenDao = getOauthTokenDao();
		Function<OauthTokenType, OauthToken> tokenFactory = new Function<OauthTokenType, OauthToken>() {
			@Override
			public OauthToken apply(OauthTokenType oauthTokenType) {
				OauthToken token = oauthTokenDao.findByUserAndType(user, oauthTokenType);
				if (token == null) {
					token = new OauthToken(oauthTokenType, user);
				}
				return token;
			}
		}; 
		OauthToken accessToken = tokenFactory.apply(OauthTokenType.ACCESS);
		OauthToken refreshToken = tokenFactory.apply(OauthTokenType.REFRESH);
		TokenResponse tokenResponse = requestToken("code", successCode, "authorization_code", true);
		accessToken.setToken(tokenResponse.getAccessToken());
		accessToken.setExpiryDate(tokenResponse.getExpiryDate());
		refreshToken.setToken(tokenResponse.getRefreshToken());
		for (OauthToken token : new OauthToken[] { accessToken, refreshToken }) {
			oauthTokenDao.store(token);
		}
	}

	/**
	 * @param accessToken
	 * @throws GoogleAuthenticationFailedException 
	 * @throws IOException 
	 */
	protected void updateAccessToken(User user, OauthToken accessToken) throws GoogleAuthenticationFailedException, IOException {
		OauthToken refreshToken = getOauthTokenDao().findByUserAndType(user, OauthTokenType.REFRESH);
		if (refreshToken == null) {
			throw new GoogleAuthenticationFailedException("No refresh token found.");
		}
		TokenResponse tokenResponse = requestToken("refresh_token", refreshToken.getToken(), "refresh_token", false);
		accessToken.setToken(tokenResponse.getAccessToken());
		accessToken.setExpiryDate(tokenResponse.getExpiryDate());
		getOauthTokenDao().store(accessToken);
	}

	protected TokenResponse requestToken(String tokenType, String token, String grantType, boolean includeRedirect) throws IOException, GoogleAuthenticationFailedException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(GOOGLE_OAUTH_TOKEN_URL);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("client_id", getClientId()));
		formparams.add(new BasicNameValuePair("client_secret", CONSUMER_SECRET));
		formparams.add(new BasicNameValuePair(tokenType, token));
		if (includeRedirect) {
			formparams.add(new BasicNameValuePair("redirect_uri", "urn:ietf:wg:oauth:2.0:oob"));
		}
		formparams.add(new BasicNameValuePair("grant_type", grantType));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new GoogleAuthenticationFailedException("Requesting a token refresh resulted in a http status of " + statusCode);
		}
		Reader reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
		try {
			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
			return gson.fromJson(reader, TokenResponse.class);
		}
		finally {
			Closeables.closeQuietly(reader);
		}
	}

	@Override
	public String getClientId() {
		return Constants.CONSUMER_KEY;
	}
	
	public UserDao getUserDao() {
		return i_userDao;
	}

	public void setUserDao(UserDao userDao) {
		i_userDao = userDao;
	}

	public OauthTokenDao getOauthTokenDao() {
		return i_oauthTokenDao;
	}

	public void setOauthTokenDao(OauthTokenDao oauthTokenDao) {
		i_oauthTokenDao = oauthTokenDao;
	}
}
