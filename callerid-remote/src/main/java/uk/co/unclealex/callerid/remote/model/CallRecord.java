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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A persisted representation of a call that has been received.
 */
@Entity
public class CallRecord {

  /** The id. */
  @Id
  @GeneratedValue
  private Integer id;

  /**
   * The date and time this call was received.
   */
  @Column(nullable = false, unique = true)
  private Date callDate;

  /**
   * The telephone number that called.
   */
  @Column(nullable = false)
  private String telephoneNumber;

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((callDate == null) ? 0 : callDate.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((telephoneNumber == null) ? 0 : telephoneNumber.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final CallRecord other = (CallRecord) obj;
    if (callDate == null) {
      if (other.callDate != null)
        return false;
    }
    else if (!callDate.equals(other.callDate))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    }
    else if (!id.equals(other.id))
      return false;
    if (telephoneNumber == null) {
      if (other.telephoneNumber != null)
        return false;
    }
    else if (!telephoneNumber.equals(other.telephoneNumber))
      return false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "CallRecord [id=" + id + ", callDate=" + callDate + ", telephoneNumber=" + telephoneNumber + "]";
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
