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

package uk.co.unclealex.callerid.calls.model;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

/**
 * A model bean that contains all the known information about a received call. This can be either from a call
 * received from a modem or from a previously received call in the database.
 * @author alex
 * 
 */
public class ReceivedCall {

  /**
   * The instant the call was recevied.
   */
  private final DateTime callReceivedTime;

  /**
   * The {@link PhoneNumber} that made the call.
   */
  private final PhoneNumber phoneNumber;

  /**
   * The most recently manually attributed contact name to this number or null
   * if no name has been manually attributed.
   */
  private final String contactName;

  /**
   * A list of {@link Contact}s to who may be calling.
   */
  private final List<Contact> contacts;

  /**
   * Instantiates a new received call.
   * 
   * @param callReceivedTime
   *          the call received time
   * @param phoneNumber
   *          the phone number
   * @param contactName
   *          the contact name
   * @param contacts
   *          the contacts
   */
  public ReceivedCall(DateTime callReceivedTime, PhoneNumber phoneNumber, String contactName, List<Contact> contacts) {
    super();
    this.callReceivedTime = callReceivedTime;
    this.phoneNumber = phoneNumber;
    this.contactName = contactName;
    this.contacts = contacts;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * Gets the instant the call was recevied.
   * 
   * @return the instant the call was recevied
   */
  public DateTime getCallReceivedTime() {
    return callReceivedTime;
  }

  /**
   * Gets the {@link PhoneNumber} that made the call.
   * 
   * @return the {@link PhoneNumber} that made the call
   */
  public PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Gets the most recently manually attributed contact name to this number or
   * null if no name has been manually attributed.
   * 
   * @return the most recently manually attributed contact name to this number
   *         or null if no name has been manually attributed
   */
  public String getContactName() {
    return contactName;
  }

  /**
   * Gets the a list of {@link Contact}s to who may be calling.
   * 
   * @return the a list of {@link Contact}s to who may be calling
   */
  public List<Contact> getContacts() {
    return contacts;
  }
  
  
}
