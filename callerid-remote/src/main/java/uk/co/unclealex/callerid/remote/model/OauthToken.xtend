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

import static extension org.apache.commons.lang3.builder.EqualsBuilder.*
import static extension org.apache.commons.lang3.builder.HashCodeBuilder.*
import static extension org.apache.commons.lang3.builder.ToStringBuilder.*

/**
 * A persisted representation of an OAuth token that has been handed out by Google for single sign on.
 */
//@Entity
class OauthToken {

    //@Id
    //@GeneratedValue
    //@Column(name="id")
    @Property var Integer id

    /**
     * The type of the token being persisted.
     */
    //@Enumerated(EnumType::STRING)
    //@Column(name="tokenType", nullable=false)
	@Property var OauthTokenType tokenType;

    /**
     * The value of the token being persisted.
     */
    //@Column(name="token", nullable=false)
	@Property var String token;

    /**
     * The date and time that the token expires or null if the token does not expire.
     */
    //@Column(name="expiryDate")
	@Property var Date expiryDate;
	
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
