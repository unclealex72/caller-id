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
 */

package uk.co.unclealex.callerid.phonenumber.model;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A telephone number encapsulates the three parts of a telephone number: the
 * local number, the STD code and the international prefix.
 * 
 * @author alex
 * 
 */
@PersistenceCapable
@EmbeddedOnly
public class TelephoneNumber {

  /**
   * The local part of a telephone number.
   */
  private String number;

  /**
   * The telephone number's STD code.
   */
  private String stdCode;

  /**
   * The telephone number's international prefix.
   */
  private String internationalPrefix;

  /**
   * Default constructor for serlialisation.
   */
  protected TelephoneNumber() {
    super();
  }

  /**
   * Create a new telephone number.
   * 
   * @param internationalPrefix
   *          The telephone number's international prefix.
   * @param stdCode
   *          The telephone number's STD code.
   * @param number
   *          The local part of the telephone number.
   */
  public TelephoneNumber(String internationalPrefix, String stdCode, String number) {
    super();
    this.number = number;
    this.stdCode = stdCode;
    this.internationalPrefix = internationalPrefix;
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
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /**
   * Gets the local part of a telephone number.
   * 
   * @return the local part of a telephone number
   */
  public String getNumber() {
    return number;
  }

  /**
   * Gets the telephone number's STD code.
   * 
   * @return the telephone number's STD code
   */
  public String getStdCode() {
    return stdCode;
  }

  /**
   * Gets the telephone number's international prefix.
   * 
   * @return the telephone number's international prefix
   */
  public String getInternationalPrefix() {
    return internationalPrefix;
  }
}
