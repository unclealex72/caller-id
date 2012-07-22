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
package uk.co.unclealex.callerid.phonenumber.model;

import java.util.Arrays;
import java.util.List;

/**
 * A non-geographic {@link PhoneNumber}. As countries share international prefixes, a list of countries is stored.
 * @author alex
 *
 */
public class CountriesOnlyPhoneNumber extends AbstractPhoneNumber {

  /**
   * The list of countries that this call may have originated from in order of the most likely to the least likely.
   */
	private List<String> countries;
	
	/**
	 * The country code for the phone number.
	 */
	private String countryCode;
	
	/**
   * Instantiates a new countries only phone number.
	 * @param countryCode
   *          the country code
	 * @param number
   *          the number
	 * @param countries
   *          the countries
   */
	public CountriesOnlyPhoneNumber(String countryCode, String number, List<String> countries) {
		super(number);
		this.countries = countries;
		this.countryCode = countryCode;
	}

  /**
   * Instantiates a new countries only phone number.
   * @param countryCode
   *          the country code
   * @param number
   *          the number
   * @param countries
   *          the countries
   */
  public CountriesOnlyPhoneNumber(String countryCode, String number, String... countries) {
    this(countryCode, number, Arrays.asList(countries));
  }
    /**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T accept(PhoneNumber.Visitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
   * Gets the list of countries that this call may have originated from in order
   * of the most likely to the least likely.
   * 
   * @return the list of countries that this call may have originated from in
   *         order of the most likely to the least likely
   */
	public List<String> getCountries() {
		return countries;
	}

	/**
   * Gets the country code for the phone number.
   * 
   * @return the country code for the phone number
   */
	public String getCountryCode() {
		return countryCode;
	}
}
