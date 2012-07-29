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
 * A country code is the international prefix of a telephone number. A country code can be for more than one
 * {@link Country} which can then have one or more {@link AreaCode}s.
 * @author alex
 *
 */
public class CountryCode {

  /**
   * The international prefix of telephone calls originating from this country code.
   */
  private final String internationalPrefix;
  
  /**
   * Instantiates a new country code.
   * 
   * @param internationalPrefix
   *          the international prefix
   */
  public CountryCode(String internationalPrefix) {
    super();
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
   * Gets the international prefix of telephone calls originating from this
   * country code.
   * 
   * @return the international prefix of telephone calls originating from this
   *         country code
   */
  public String getInternationalPrefix() {
    return internationalPrefix;
  }

}
