/**
 * 
 */
package uk.co.unclealex.callerid.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import uk.co.unclealex.hibernate.model.KeyedBean;

/**
 * Copyright 2011 Alex Jones
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
@Entity
public class OauthToken extends KeyedBean<OauthToken> {

	public static OauthToken example() {
		return new OauthToken();
	}
	
	protected OauthToken() {
		super();
	}

	public OauthToken(OauthTokenType tokenType, User user) {
		super();
		i_tokenType = tokenType;
		i_user = user;
	}


	private OauthTokenType i_tokenType;
	private String i_token;
	private Date i_expiryDate;
	private User i_user;
	
	@Override
	public boolean equals(Object obj) {
		OauthToken other;
		return obj instanceof OauthToken && (other = (OauthToken) obj).getTokenType().equals(getTokenType()) &&
				other.getUser().equals(getUser());
				
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s:%s", getUser(), getTokenType(), getToken());
	}
	
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public OauthTokenType getTokenType() {
		return i_tokenType;
	}
	
	public void setTokenType(OauthTokenType tokenType) {
		i_tokenType = tokenType;
	}
	
	public Date getExpiryDate() {
		return i_expiryDate;
	}
	
	public void setExpiryDate(Date expiryDate) {
		i_expiryDate = expiryDate;
	}

	@Column(nullable=false)
	public String getToken() {
		return i_token;
	}

	public void setToken(String token) {
		i_token = token;
	}

	@ManyToOne(optional=false)
	public User getUser() {
		return i_user;
	}
	
	public void setUser(User user) {
		i_user = user;
	}
}
