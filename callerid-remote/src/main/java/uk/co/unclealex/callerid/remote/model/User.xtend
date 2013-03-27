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
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import uk.co.unclealex.persistence.jpa.xtend.JpaPersistable

/**
 * A persitable person who can use the system.
 */
@JpaPersistable
class User {

    /**
     * The user's Google username.
     */
    @Column(nullable=false, unique=true)
	var String username;

    /**
     * A cached copy of this user's Google contacts.
     */
    @ManyToMany
	var List<Contact> contacts;

    /**
     * The OAuth tokens given by Google single sign on.
     */
    @OneToMany(cascade=CascadeType::ALL)
	var List<OauthToken> oauthTokens;
	
}
