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

import scala.collection.JavaConversions._
import uk.co.unclealex.callerid.remote.google.GoogleContactsService
import uk.co.unclealex.callerid.remote.numbers.NumberLocationService
import uk.co.unclealex.callerid.remote.google.GoogleContact
import uk.co.unclealex.callerid.remote.dao.UserDao
import uk.co.unclealex.callerid.remote.numbers.PhoneNumber
import scala.collection.mutable.HashMap

/**
 * The default implementation of {@link ContactsService}.
 */
class ContactServiceImpl(
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

  override def asContact: PhoneNumber => Option[GoogleContact] = {
    // Eagerly build a map of contacts by phone number to avoid having to recalculate all the time.
    val allContacts =
      userDao.getAll().map(googleContactsService.getAllContacts(_)).flatten
    val contactsByPhoneNumber = new HashMap[PhoneNumber, GoogleContact]
    allContacts.foreach {
      contact =>
        val phoneNumbers = contact.telephoneNumbers.map(numberLocationService.decompose(_))
        phoneNumbers.foreach { phoneNumber => contactsByPhoneNumber += phoneNumber -> contact }
    }
    contactsByPhoneNumber.get _
  }

}