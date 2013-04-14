/**
 * Copyright 2013 Alex Jones
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
 * @author alex
 *
 */
package uk.co.unclealex.callerid.remote.model;

import java.util.Date
import javax.jdo.annotations.Column
import javax.jdo.annotations.IdGeneratorStrategy
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.PrimaryKey
import javax.validation.constraints.NotNull

import static extension org.apache.commons.lang3.builder.EqualsBuilder.*
import static extension org.apache.commons.lang3.builder.HashCodeBuilder.*
import static extension org.apache.commons.lang3.builder.ToStringBuilder.*
import com.google.common.base.Optional

/**
 * A persisted representation of an OAuth token that has been handed out by Google for single sign on.
 */
@PersistenceCapable(identityType = IdentityType::DATASTORE, table="tokens", detachable="true")
class OauthToken {

    @PrimaryKey(name="id")
    @Persistent(valueStrategy = IdGeneratorStrategy::NATIVE)
    @Property var Integer id

    /**
     * The type of the token being persisted.
     */
    @NotNull
    @Column(name="tokenType")
	@Property var OauthTokenType tokenType;

    /**
     * The value of the token being persisted.
     */
    @NotNull
    @Column(name="token")
	@Property var String token;

    /**
     * The date and time that the token expires or null if the token does not expire.
     */
    @Column(name="expiryDate")
	@Property var Optional<Date> expiryDate;
	
    override equals(Object obj) {
        reflectionEquals(obj)
    }
    
    override hashCode() {
        reflectionHashCode
    }
    
    override toString() {
        reflectionToString
    }
    
}
