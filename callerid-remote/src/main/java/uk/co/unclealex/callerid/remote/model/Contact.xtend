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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import uk.co.unclealex.persistence.jpa.xtend.JpaPersistable

/**
 * A persisted, cached version of a Google contact that can be used without having to immediately talk to Google.
 */
@JpaPersistable
class Contact {

    /**
     * The name of this contact.
     */
    @Column(nullable=false, unique=true)
	var String name;
	
	/**
	 * The known list of telephone numbers for this contact.
	 */
    @ManyToMany(mappedBy="contacts")
	var List<TelephoneNumber> telephoneNumbers;
	
	/**
	 * The users who own this contact.
	 */
    @ManyToMany(mappedBy="contacts")
	var List<User> users;
	
}
