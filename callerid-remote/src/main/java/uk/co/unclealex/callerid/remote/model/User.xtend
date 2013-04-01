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

import java.util.List
import static extension org.apache.commons.lang3.builder.EqualsBuilder.*
import static extension org.apache.commons.lang3.builder.ToStringBuilder.*
import static extension org.apache.commons.lang3.builder.HashCodeBuilder.*

/**
 * A persitable person who can use the system.
 */
//@Entity
class User {

    //@Id
    //@GeneratedValue
    //@Column(name="id")
    @Property var Integer id

    /**
     * The user's Google username.
     */
    //@Column(name="username", nullable=false, unique=true)
	@Property var String username;

    /**
     * The OAuth tokens given by Google single sign on.
     */
    //@OneToMany(cascade=CascadeType::ALL)
    //@Column(name="oauthTokens")
	@Property var List<OauthToken> oauthTokens;
	
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
