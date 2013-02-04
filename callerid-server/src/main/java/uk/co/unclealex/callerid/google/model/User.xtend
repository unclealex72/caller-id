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

package uk.co.unclealex.callerid.google.model

import java.util.List
import javax.jdo.annotations.Column
import javax.jdo.annotations.Element
import javax.jdo.annotations.Embedded
import javax.jdo.annotations.IdGeneratorStrategy
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.PrimaryKey
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.eclipse.xtend.lib.Property

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
  @Persistent(valueStrategy = IdGeneratorStrategy::NATIVE)
  @Column(name = "id")
  @Property Integer id;
  
  /**
   * The oauth tokens used to validate this user against Google APIs.
   */
	@Embedded
	@Property OAuthTokens oauthTokens;
	
	/**
	 * The user's Google username.
	 */
	 @Column(name = "username")
	@Property String username;
	
	/**
	 * The list of this user's Google contacts.
	 */
	@Element(dependent="true")
	@Column(name = "contacts")
	@Property List<Contact> contacts;
	
  /**
   * Create a new user.
   * @param username The user's Google username.
   */
  public new(String username, OAuthTokens oAuthTokens, List<Contact> contacts) {
    super();
    this.username = username;
    this.oauthTokens = oAuthTokens;
    this.contacts = contacts;
  }
	
	/**
	 * {@inheritDoc}
	 */
	override equals(Object obj) {
	  EqualsBuilder::reflectionEquals(this, obj);
	}

	/**
	 * {@inheritDoc}
	 */
	override hashCode() {
	  HashCodeBuilder::reflectionHashCode(this);
	}

	/**
	 * {@inheritDoc}
	 */
	override toString() {
	  ToStringBuilder::reflectionToString(this);
	}
}
