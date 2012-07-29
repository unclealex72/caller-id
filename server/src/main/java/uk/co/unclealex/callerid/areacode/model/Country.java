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

package uk.co.unclealex.callerid.areacode.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A country of an {@link AreaCode}.
 * @author alex
 *
 */
public class Country {

  /**
   * The name of this country.
   */
  private final String name;

  /**
   * The country code for this country.
   */
  private final CountryCode countryCode;

  /**
   * The top level domain for this country.
   */
  private final String tld;
  
  /**
   * Instantiates a new country.
   * 
   * @param name
   *          the name
   * @param countryCode
   *          the country code
   * @param tld
   *          the tld
   */
  public Country(String name, CountryCode countryCode, String tld) {
    super();
    this.name = name;
    this.countryCode = countryCode;
    this.tld = tld;
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
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * Gets the name of this country.
   * 
   * @return the name of this country
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the country code for this country.
   * 
   * @return the country code for this country
   */
  public CountryCode getCountryCode() {
    return countryCode;
  }

  /**
   * Gets the top level domain for this country.
   * 
   * @return the top level domain for this country
   */
  public String getTld() {
    return tld;
  }

  
}
