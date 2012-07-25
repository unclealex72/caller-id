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
 * @author alex
 *
 */

package uk.co.unclealex.callerid.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;

import uk.co.unclealex.callerid.calls.dao.CallDao;
import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.google.dao.ContactDao;
import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

import com.google.common.base.Function;

/**
 * The default implementation of {@link ReceivedCallFactory}.
 * 
 * @author alex
 * 
 */
public class ReceivedCallFactoryImpl implements ReceivedCallFactory {

  /**
   * The {@link PhoneNumberFactory} used to generate a {@link PhoneNumber}.
   */
  private final PhoneNumberFactory phoneNumberFactory;

  /**
   * The {@link CallDao} used to locate calls.
   */
  private final CallDao callDao;

  /**
   * The {@link ContactDao} used to search for contacts who may be associated
   * with a call.
   */
  private final ContactDao contactDao;

  /**
   * A function that transforms {@link PhoneNumber}s into their fully specified
   * number. (e.g. 441256733277)
   */
  private final Function<PhoneNumber, String> phoneNumberNormaliser;

  /**
   * 
   * @param phoneNumberFactory
   *          the phone number factory
   * @param callDao
   *          the call dao
   * @param contactDao
   *          the contact dao
   * @param phoneNumberNormaliser
   *          the phone number normaliser
   */
  @Inject
  public ReceivedCallFactoryImpl(
      PhoneNumberFactory phoneNumberFactory,
      CallDao callDao,
      ContactDao contactDao,
      @Named("phoneNumberNormaliser") Function<PhoneNumber, String> phoneNumberNormaliser) {
    super();
    this.phoneNumberFactory = phoneNumberFactory;
    this.callDao = callDao;
    this.contactDao = contactDao;
    this.phoneNumberNormaliser = phoneNumberNormaliser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReceivedCall create(final Call call) {
    Function<String, String> thisCallFunction = new Function<String, String>() {
      @Override
      public String apply(String phoneNumber) {
        return call.getContactName();
      }
    };
    return create(call.getCallTime(), thisCallFunction, call.getTelephoneNumber());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReceivedCall create(DateTime receivedCallTime, String callingNumber) {
    Function<String, String> mostRecentFunction = new Function<String, String>() {
      public String apply(String phoneNumber) {
        return getCallDao().getMostRecentContactNameForPhoneNumber(phoneNumber);
      }
    };
    return create(receivedCallTime, mostRecentFunction, callingNumber);
  }

  /**
   * Create a new {@link ReceivedCall}.
   * 
   * @param receivedCallTime
   *          The time the call was received.
   * @param contactNameFunction
   *          A function that converts the normalised calling number into a
   *          contact name.
   * @param callingNumber
   *          The number that is calling.
   * @return A new {@link ReceivedCall} will the above information as well as
   *         contact information.
   */
  protected ReceivedCall create(
      DateTime receivedCallTime,
      Function<String, String> contactNameFunction,
      String callingNumber) {
    PhoneNumber phoneNumber = getPhoneNumberFactory().create(callingNumber);
    String normalisedPhoneNumber = getPhoneNumberNormaliser().apply(phoneNumber);
    List<Contact> contacts = getContactDao().findByTelephoneNumber(normalisedPhoneNumber);
    return new ReceivedCall(receivedCallTime, phoneNumber, contactNameFunction.apply(normalisedPhoneNumber), contacts);
  }

  /**
   * Gets the {@link PhoneNumberFactory} used to generate a {@link PhoneNumber}.
   * 
   * @return the {@link PhoneNumberFactory} used to generate a
   *         {@link PhoneNumber}
   */
  public PhoneNumberFactory getPhoneNumberFactory() {
    return phoneNumberFactory;
  }

  /**
   * Gets the {@link CallDao} used to locate calls.
   * 
   * @return the {@link CallDao} used to locate calls
   */
  public CallDao getCallDao() {
    return callDao;
  }

  /**
   * Gets the {@link ContactDao} used to search for contacts who may be
   * associated with a call.
   * 
   * @return the {@link ContactDao} used to search for contacts who may be
   *         associated with a call
   */
  public ContactDao getContactDao() {
    return contactDao;
  }

  /**
   * Gets the a function that transforms {@link PhoneNumber}s into their fully
   * specified number.
   * 
   * @return the a function that transforms {@link PhoneNumber}s into their
   *         fully specified number
   */
  public Function<PhoneNumber, String> getPhoneNumberNormaliser() {
    return phoneNumberNormaliser;
  }

}
