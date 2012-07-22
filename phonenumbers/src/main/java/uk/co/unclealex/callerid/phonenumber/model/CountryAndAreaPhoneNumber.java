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


/**
 * A {@link PhoneNumber} where both the country and area codes are both known.
 * @author alex
 *
 */
public class CountryAndAreaPhoneNumber extends AbstractPhoneNumber {

  /**
   * The country from where calls from this number originate.
   */
	private String country;
	
  /**
   * The area within the country from where calls from this number originate.
   */	
	private String area;
	
  /**
   * The country code from where calls from this number originate.
   */	
	private String countryCode;
	
  /**
   * The area code from where calls from this number originate.
   */ 	
	private String areaCode;
	
	/**
   * Instantiates a new country and area phone number.
   * 
   * @param country
   *          the country
   * @param area
   *          the area
   * @param countryCode
   *          the country code
   * @param areaCode
   *          the area code
   * @param number
   *          the number
   */
	public CountryAndAreaPhoneNumber(String country, String area, String countryCode, String areaCode, String number) {
		super(number);
		this.country = country;
		this.area = area;
		this.countryCode = countryCode;
		this.areaCode = areaCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T accept(PhoneNumber.Visitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
   * Gets the country from where calls from this number originate.
   * 
   * @return the country from where calls from this number originate
   */
	public String getCountry() {
		return country;
	}

	/**
   * Gets the area within the country from where calls from this number
   * originate.
   * 
   * @return the area within the country from where calls from this number
   *         originate
   */
	public String getArea() {
		return area;
	}

	/**
   * Gets the country code from where calls from this number originate.
   * 
   * @return the country code from where calls from this number originate
   */
	public String getCountryCode() {
		return countryCode;
	}

	/**
   * Gets the area code from where calls from this number originate.
   * 
   * @return the area code from where calls from this number originate
   */
	public String getAreaCode() {
		return areaCode;
	}
}
