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

import java.util.Date;

import javax.jdo.annotations.Column;
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

/**
 * A persisted representation of a call that has been received.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, table = "calls", detachable = "true")
public class CallRecord {

  /** The id. */
  @PrimaryKey(name = "id")
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;

  /**
   * The date and time this call was received.
   */
  @NotNull
  @Unique
  @Column(name = "callDate")
  private Date callDate;

  /**
   * The telephone number that called.
   */
  @NotNull
  @Column(name = "telephoneNumber")
  private String telephoneNumber;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
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
   * Gets the id.
   * 
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the id.
   * 
   * @param id
   *          the new id
   */
  public void setId(final Integer id) {
    this.id = id;
  }

  /**
   * Gets the date and time this call was received.
   * 
   * @return the date and time this call was received
   */
  public Date getCallDate() {
    return callDate;
  }

  /**
   * Sets the date and time this call was received.
   * 
   * @param callDate
   *          the new date and time this call was received
   */
  public void setCallDate(final Date callDate) {
    this.callDate = callDate;
  }

  /**
   * Gets the telephone number that called.
   * 
   * @return the telephone number that called
   */
  public String getTelephoneNumber() {
    return telephoneNumber;
  }

  /**
   * Sets the telephone number that called.
   * 
   * @param telephoneNumber
   *          the new telephone number that called
   */
  public void setTelephoneNumber(final String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }

}
