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
package legacy.remote.contact

import scala.collection.JavaConversions._
import legacy.remote.google.GoogleContactsService
import legacy.remote.number.NumberLocationService
import legacy.remote.google.GoogleContact
import legacy.remote.dao.UserDao
import legacy.remote.number.PhoneNumber
import scala.collection.mutable.HashMap
import javax.inject.Inject

/**
 * The default implementation of {@link ContactsService}.
 */
class ContactServiceImpl @Inject() (
  /**
   * The {@link GoogleContactsService} used to query Google for user contacts.
   */
  googleContactsService: GoogleContactsService,
  /**
   * The {@link NumberLocationService} used to convert string phone numbers into normalised {@link PhoneNumber}s.
   */
  numberLocationService: NumberLocationService,
  /**
   * The {@link UserDao} used to get all known Google users.
   */
  userDao: UserDao) extends ContactService {

  override def contactsByNormalisedPhoneNumber = {
    val allContacts =
      userDao.getAll.map(googleContactsService.getAllContacts _).flatten
    val contactsByPhoneNumber = new HashMap[String, Contact]
    allContacts.foreach {
      contact =>
        val phoneNumbers = contact.telephoneNumbers.map(numberLocationService.decompose _)
        phoneNumbers.foreach {
          phoneNumber => contactsByPhoneNumber += phoneNumber.normalisedNumber -> Contact(contact.name, contact.address)
        }
    }
    contactsByPhoneNumber.toMap
  }
}