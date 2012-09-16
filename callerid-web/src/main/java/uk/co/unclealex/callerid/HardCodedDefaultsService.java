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

package uk.co.unclealex.callerid;

import uk.co.unclealex.callerid.defaults.DefaultsService;

/**
 * A hard-coded bean implementation of the {@link DefaultsService}. This implementation allows defaults to be
 * defined in a Spring application context.
 * @author alex
 *
 */
public class HardCodedDefaultsService implements DefaultsService {

  /**
   * The country code of the telephone that is receiving calls.
   */
  private String countryCode;
  
  /**
   * The area code of the telephone that is receiving calls.
   */
  private String areaCode;
  
  /**
   * The prefix for international calls. For the UK this is <i>00</i>.
   */
  private String internationalPrefix;
  
  /**
   * The prefix for non-local national calls. For the UK this is <i>0</i>.
   */
  private String areaCodePrefix;
  
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getAreaCode() {
    return areaCode;
  }

  public void setAreaCode(String areaCode) {
    this.areaCode = areaCode;
  }

  public String getInternationalPrefix() {
    return internationalPrefix;
  }

  public void setInternationalPrefix(String internationalPrefix) {
    this.internationalPrefix = internationalPrefix;
  }

  public String getAreaCodePrefix() {
    return areaCodePrefix;
  }

  public void setAreaCodePrefix(String areaCodePrefix) {
    this.areaCodePrefix = areaCodePrefix;
  }

  
}
