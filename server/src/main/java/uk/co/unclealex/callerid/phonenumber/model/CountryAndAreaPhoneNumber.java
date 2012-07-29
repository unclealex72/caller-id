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

import uk.co.unclealex.callerid.areacode.model.AreaCode;

/**
 * A {@link PhoneNumber} where both the country and area codes are both known.
 * @author alex
 *
 */
public class CountryAndAreaPhoneNumber extends AbstractPhoneNumber {

  /**
   * The {@link AreaCode} for where this phone number originated.
   */
	private final AreaCode areaCode;	

	/**
   * Instantiates a new country and area phone number.
   * 
   * @param areaCode
   *          the area code
   * @param number
   *          the number
   */
	public CountryAndAreaPhoneNumber(AreaCode areaCode, String number) {
		super(number);
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
   * Gets the {@link AreaCode} for where this phone number originated.
   * 
   * @return the {@link AreaCode} for where this phone number originated
   */
  public AreaCode getAreaCode() {
    return areaCode;
  }
}
