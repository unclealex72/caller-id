/**
 * 
 */
package uk.co.unclealex.callerid.server.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name="users")
public class User extends BusinessKeyedBean<User, String> {

	public static User example() {
		return new User();
	}

	private String i_username;
	private List<Contact> i_contacts;
	private List<OauthToken> i_oauthTokens;
	
	protected User() {
		super();
	}
	
	@Override
	@Transient
	public String getBusinessKey() {
		return getUsername();
	}
	
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	@Column(nullable=false, unique=true)
	public String getUsername() {
		return i_username;
	}

	public void setUsername(String username) {
		i_username = username;
	}

	@ManyToMany
	public List<Contact> getContacts() {
		return i_contacts;
	}

	public void setContacts(List<Contact> contacts) {
		i_contacts = contacts;
	}

	@OneToMany(cascade=CascadeType.ALL)
	public List<OauthToken> getOauthTokens() {
		return i_oauthTokens;
	}

	public void setOauthTokens(List<OauthToken> oauthTokens) {
		i_oauthTokens = oauthTokens;
	}
}
