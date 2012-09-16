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

package uk.co.unclealex.callerid.areacode.dao;

import java.util.List;
import java.util.SortedSet;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;

/**
 * @author alex
 * 
 */
public interface AreaCodeDao {

  /**
   * Get all available country codes in order of longest to shortest.
   * 
   * @return all available country codes in order of longest to shortest.
   */
  public SortedSet<String> getAllCountryCodes();

  /**
   * Get all available countries for a country code in order of the country with
   * the most area codes first and the least last.
   * 
   * @return all available countries for a country code in order of the country
   *         with the most area codes first an the least last.
   */
  public SortedSet<Country> getAllCountriesForCountryCode(String countryCode);

  /**
   * Get all {@link AreaCode}s for a given country code in order of the longest
   * area code prefix first and the shortest last.
   * 
   * @param countryCode
   *          The required country code.
   * @return All {@link AreaCode}s for a given country code in order of the longest
   * area code prefix first and the shortest last.
   */
  public SortedSet<AreaCode> getAllAreaCodesForCountryCode(String countryCode);

  /**
   * Get all known {@link AreaCodes}.
   * @return All known {@link AreaCodes}.
   */
  public List<AreaCode> getAll();
}
