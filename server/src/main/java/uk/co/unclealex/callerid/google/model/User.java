/**
 * Copyright 2012 Alex Jones
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

package uk.co.unclealex.callerid.google.model;

import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A user represents a Google user who can use this application.
 * @author alex
 *
 */
@PersistenceCapable
public class User {

  /**
   * The contact's primary id.
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;
  
  /**
   * The oauth tokens used to validate this user against Google APIs.
   */
	@Embedded
	private OAuthTokens oauthTokens;
	
	/**
	 * The user's Google username.
	 */
	private String username;
	
	/**
	 * The list of this user's Google contacts.
	 */
	@Element(dependent="true")
	private List<Contact> contacts;
	
	/**
	 * Default constructor for serialisation.
	 */
	protected User() {
	  super();
	}

  /**
   * Create a new user.
   * @param username The user's Google username.
   */
  public User(String username) {
    super();
    this.username = username;
  }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
	  return EqualsBuilder.reflectionEquals(this, obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
	  return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
	  return ToStringBuilder.reflectionToString(this);
	}

	/**
   * Gets the contact's primary id.
   * 
   * @return the contact's primary id
   */
	public Integer getId() {
    return id;
  }

  /**
   * Sets the contact's primary id.
   * 
   * @param id
   *          the new contact's primary id
   */
  protected void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets the oauth tokens used to validate this user against Google APIs.
   * 
   * @return the oauth tokens used to validate this user against Google APIs
   */
  public OAuthTokens getOauthTokens() {
    return oauthTokens;
  }

  /**
   * Sets the oauth tokens used to validate this user against Google APIs.
   * 
   * @param oauthTokens
   *          the new oauth tokens used to validate this user against Google
   *          APIs
   */
  public void setOauthTokens(OAuthTokens oauthTokens) {
    this.oauthTokens = oauthTokens;
  }

  /**
   * Gets the user's Google username.
   * 
   * @return the user's Google username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the user's Google username.
   * 
   * @param username
   *          the new user's Google username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the list of this user's Google contacts.
   * 
   * @return the list of this user's Google contacts
   */
  public List<Contact> getContacts() {
    return contacts;
  }

  /**
   * Sets the list of this user's Google contacts.
   * 
   * @param contacts
   *          the new list of this user's Google contacts
   */
  public void setContacts(List<Contact> contacts) {
    this.contacts = contacts;
  }
	
	
}
