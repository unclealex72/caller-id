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

package uk.co.unclealex.callerid.phonenumber.dao;

import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

/**
 * A data access object for {@link TelephoneNumber}s. Note that this is not really expected to be used directly but is
 * included for testing annotations on {@link TelephoneNumber}.
 * @author alex
 *
 */
public interface TelephoneNumberDao {

	/**
	 * Find a {@link TelephoneNumber} by its full number.
	 * @param internationalPrefix The international prefix of the {@link TelephoneNumber} to find.
	 * @param stdCode The STD code of the {@link TelephoneNumber} to find.
	 * @param number The local number of the {@link TelephoneNumber} to find.
	 * @return The {@link TelephoneNumber} with the given attributes or null if one could not be found.
	 */
	public TelephoneNumber findByNumber(String internationalPrefix, String stdCode, String number);
	
	/**
	 * Store a {@link TelephoneNumber}.
	 * @param telephoneNumber The {@link TelephoneNumber} to persist.
	 */
	public void store(TelephoneNumber telephoneNumber);
	
	/**
	 * Remove a {@link TelephoneNumber}.
	 */
	public void remove(TelephoneNumber telephoneNumber);
}
