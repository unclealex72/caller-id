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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

/**
 * A class that models received telephone calls.
 * 
 * @author alex
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class Call {

  /**
   * The call's primary id.
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;

  /**
   * The time at which this call was received.
   */
  @NotNull
  @Unique
  @Persistent
  private DateTime callTime;
  
  /**
   * The telephone number who called or null if the number was withheld.
   */
  private TelephoneNumber telephoneNumber;
  
  /**
   * The name of the contact associated with this number. This may be derived from the known list of contacts or user entered.
   */
  private String contactName;

  /**
   * Instantiates a new call.
   * 
   * @param callTime
   *          the call time
   * @param telephoneNumber
   *          the telephone number
   * @param contactName
   *          the contact name
   */
  public Call(DateTime callTime, TelephoneNumber telephoneNumber, String contactName) {
    super();
    this.callTime = callTime;
    this.telephoneNumber = telephoneNumber;
    this.contactName = contactName;
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
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /**
   * Gets the call's primary id.
   * 
   * @return the call's primary id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the call's primary id.
   * 
   * @param id
   *          the new call's primary id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets the time at which this call was received.
   * 
   * @return the time at which this call was received
   */
  public DateTime getCallTime() {
    return callTime;
  }

  /**
   * Sets the time at which this call was received.
   * 
   * @param callTime
   *          the new time at which this call was received
   */
  public void setCallTime(DateTime callTime) {
    this.callTime = callTime;
  }

  /**
   * Gets the telephone number who called or null if the number was withheld.
   * 
   * @return the telephone number who called or null if the number was withheld
   */
  public TelephoneNumber getTelephoneNumber() {
    return telephoneNumber;
  }

  /**
   * Sets the telephone number who called or null if the number was withheld.
   * 
   * @param telephoneNumber
   *          the new telephone number who called or null if the number was
   *          withheld
   */
  public void setTelephoneNumber(TelephoneNumber telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }

  /**
   * Gets the name of the contact associated with this number.
   * 
   * @return the name of the contact associated with this number
   */
  public String getContactName() {
    return contactName;
  }

  /**
   * Sets the name of the contact associated with this number.
   * 
   * @param contactName
   *          the new name of the contact associated with this number
   */
  public void setContactName(String contactName) {
    this.contactName = contactName;
  }


}
