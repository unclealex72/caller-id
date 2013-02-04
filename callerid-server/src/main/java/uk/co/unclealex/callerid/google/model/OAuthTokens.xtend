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
 */

package uk.co.unclealex.callerid.google.model

import javax.jdo.annotations.EmbeddedOnly
import javax.jdo.annotations.PersistenceCapable
import org.eclipse.xtend.lib.Property
import javax.jdo.annotations.Column
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.EqualsBuilder

/**
 * A class that stores the two OAuth tokens required for by Google for a
 * {@link User}.
 * 
 * @author alex
 * 
 */
@PersistenceCapable
@EmbeddedOnly
public class OAuthTokens {

	/**
	 * The user's access token.
	 */
	@Column(name = "accessToken")
	@Property String accessToken;

	/**
	 * The user's refresh token.
	 */
    @Column(name = "refreshToken")
	@Property String refreshToken;

	/**
	 * A default constructor for persistence.
	 */
	protected new() {
		super();
	}

	/**
	 * Create a new instance.
	 * 
	 * @param accessToken
	 *          The user's access token.
	 * @param refreshToken
	 *          The user's refresh token.
	 */
	public new(String accessToken, String refreshToken) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	/**
	 * {@inheritDoc}
	 */
	override toString() {
	    ToStringBuilder::reflectionToString(this)
	}

	/**
	 * {@inheritDoc}
	 */
	override hashCode() {
	    HashCodeBuilder::reflectionHashCode(this)
	}

	/**
	 * {@inheritDoc}
	 */
	override equals(Object obj) {
	    EqualsBuilder::reflectionEquals(this, obj)
	}

}
