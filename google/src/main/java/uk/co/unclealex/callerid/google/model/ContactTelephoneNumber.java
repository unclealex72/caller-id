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

package uk.co.unclealex.callerid.google.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

import com.google.common.base.Function;

/**
 * A container to allow {@link Contact}s to own more than one {@link TelephoneNumber}.
 * @author alex
 *
 */
@PersistenceCapable
public class ContactTelephoneNumber {

  /**
   * A {@link Function} to allow {@link TelephoneNumber}s to {@link Contact}s upon construction.
   */
  static Function<TelephoneNumber, ContactTelephoneNumber> FROM_NUMBER = new Function<TelephoneNumber, ContactTelephoneNumber>() {
    @Override
    public ContactTelephoneNumber apply(TelephoneNumber telephoneNumber) {
      return new ContactTelephoneNumber(telephoneNumber);
    }
  };
  /**
   * The contact's primary id.
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;

  /**
   * The telephone number to be owned by the contact.
   */
  private TelephoneNumber telephoneNumber;

  /**
   * @param telephoneNumber
   */
  public ContactTelephoneNumber(TelephoneNumber telephoneNumber) {
    super();
    this.telephoneNumber = telephoneNumber;
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public TelephoneNumber getTelephoneNumber() {
    return telephoneNumber;
  }

  public void setTelephoneNumber(TelephoneNumber telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }

}
