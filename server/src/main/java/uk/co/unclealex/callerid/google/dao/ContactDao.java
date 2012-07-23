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

package uk.co.unclealex.callerid.google.dao;

import java.util.List;

import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.persistence.dao.BasicDao;

/**
 * The data access object for persisting {@link Contact}s.
 * @author alex
 *
 */
public interface ContactDao extends BasicDao<Contact> {

	/**
	 * Find all {@link Contact}s who have the given telephone number.
	 * @param telephoneNumber The telephoneNumber to search for.
	 * @return The list of all {@link Contact}s who have the given telephone number.
	 */
	public List<Contact> findByTelephoneNumber(String telephoneNumber);
	
	/**
	 * Find a {@link Contact} by their unique name.
	 * @param name The name of the {@link Contact} to look for.
	 * @return The {@link Contact} with the given name or null if no such contact exists.
	 */
	public Contact findByName(String name);
}
