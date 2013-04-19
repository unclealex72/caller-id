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
package uk.co.unclealex.callerid.remote.contact

import com.google.common.base.Optional
import com.google.gdata.client.contacts.ContactsService
import java.util.Map
import org.eclipse.xtext.xbase.lib.Functions
import uk.co.unclealex.callerid.remote.dao.UserDao
import uk.co.unclealex.callerid.remote.google.GoogleContact
import uk.co.unclealex.callerid.remote.google.GoogleContactsService
import uk.co.unclealex.callerid.remote.numbers.NumberLocationService
import uk.co.unclealex.callerid.remote.numbers.PhoneNumber

import static extension com.google.common.base.Optional.*
import static extension uk.co.unclealex.xtend.OptionalExtensions.*

/**
 * The default implementation of {@link ContactsService}.
 */
class ContactServiceImpl implements ContactService {
    
    /**
     * The {@link GoogleContactsService} used to query Google for user contacts.
     */
    @Property var extension GoogleContactsService googleContactsService
    
    /**
     * The {@link NumberLocationService} used to convert string phone numbers into normalised {@link PhoneNumber}s.
     */
    @Property var extension NumberLocationService numberLocationService
    
    /**
     * The {@link UserDao} used to get all known Google users.
     */
    @Property var UserDao userDao
    
    override Functions$Function1<PhoneNumber, Optional<GoogleContact>> asContacts() {
        // Eagerly build a map of contacts by phone number to avoid having to recalculate all the time.
        val Map<PhoneNumber, GoogleContact> contactsByPhoneNumber = newHashMap()
        userDao.all.map[getAllContacts].flatten.forEach[GoogleContact googleContact |
            googleContact.telephoneNumbers.map[decompose].presentInstances.forEach[PhoneNumber phoneNumber |
                contactsByPhoneNumber.put(phoneNumber, googleContact)
            ]
        ]
        [contactsByPhoneNumber.optionalGet(it)]
    }
    
}