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
import java.util.List;
import java.util.Map;

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
import uk.co.unclealex.callerid.server.model.GoogleContact;
import uk.co.unclealex.callerid.server.model.OauthToken;
import uk.co.unclealex.callerid.server.model.OauthTokenType;
import uk.co.unclealex.callerid.server.model.User;
import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.callerid.shared.service.Constants;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
	public Map<User, List<GoogleContact>> getAllContactsByUser() throws GoogleAuthenticationFailedException, IOException {
		Map<User, List<GoogleContact>> contactsByUser = Maps.newTreeMap();
		for (User user : getUserDao().getAll()) {
			try {
				contactsByUser.put(user, getAllContactsForUser(user));
			}
			catch (ServiceException e) {
				throw new IOException(e);
			}
		}
		return contactsByUser;
	}

	protected List<GoogleContact> getAllContactsForUser(User user) throws IOException, ServiceException, GoogleAuthenticationFailedException {
		ContactsService contactsService = createContactsService(user);
	  ContactFeed resultFeed = contactsService.getFeed(new URL(Constants.CONTACTS_FEED), ContactFeed.class);
	  List<GoogleContact> googleContacts = Lists.newArrayList();
	  for (ContactEntry entry : resultFeed.getEntries()) {
	  	if (entry.hasName()) {
	  		String name = entry.getName().getFullName().getValue();
	  		for (PhoneNumber phoneNumber : entry.getPhoneNumbers()) {
	  			String number = phoneNumber.getPhoneNumber();
	  			googleContacts.add(new GoogleContact(name, number));
	  		}
	  	}
	  }
	  return googleContacts;
	}
	
	protected ContactsService createContactsService(User user) throws GoogleAuthenticationFailedException, IOException {
		return new ContactsService("callerid.unclealex.co.uk", getOauthToken(user));
	}
	
	protected String getOauthToken(User user) throws GoogleAuthenticationFailedException, IOException {
		OauthToken accessToken = findTokenByUserAndType(user, OauthTokenType.ACCESS);
		if (accessToken == null || accessToken.getExpiryDate().getTime() - EXPIRY_LEEWAY < System.currentTimeMillis()) {
			if (accessToken == null) {
				accessToken = new OauthToken(OauthTokenType.ACCESS);
			}
			updateAccessToken(user, accessToken);
		}
		return accessToken.getToken();
	}

	@Override
	public void installSuccessCode(final User user, String successCode) throws IOException, GoogleAuthenticationFailedException {
		Function<OauthTokenType, OauthToken> tokenFactory = new Function<OauthTokenType, OauthToken>() {
			@Override
			public OauthToken apply(final OauthTokenType oauthTokenType) {
				OauthToken token = findTokenByUserAndType(user, oauthTokenType);
				if (token == null) {
					token = new OauthToken(oauthTokenType);
					user.getOauthTokens().add(token);
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
		getUserDao().store(user);
	}

	/**
	 * @param accessToken
	 * @throws GoogleAuthenticationFailedException 
	 * @throws IOException 
	 */
	protected void updateAccessToken(User user, OauthToken accessToken) throws GoogleAuthenticationFailedException, IOException {
		OauthToken refreshToken = findTokenByUserAndType(user, OauthTokenType.REFRESH);
		if (refreshToken == null) {
			throw new GoogleAuthenticationFailedException("No refresh token found.");
		}
		TokenResponse tokenResponse = requestToken("refresh_token", refreshToken.getToken(), "refresh_token", false);
		accessToken.setToken(tokenResponse.getAccessToken());
		accessToken.setExpiryDate(tokenResponse.getExpiryDate());
		getUserDao().store(user);
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

	protected OauthToken findTokenByUserAndType(final User user, final OauthTokenType oauthTokenType) {
		Predicate<OauthToken> predicate = new Predicate<OauthToken>() {
			@Override
			public boolean apply(OauthToken oauthToken) {
				return oauthTokenType.equals(oauthToken.getTokenType());
			}
		};
		OauthToken token = Iterables.find(user.getOauthTokens(), predicate, null);
		return token;
	}
}
