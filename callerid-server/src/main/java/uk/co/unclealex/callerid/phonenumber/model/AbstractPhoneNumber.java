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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A base class for {@link PhoneNumber}s.
 * @author alex
 *
 */
public abstract class AbstractPhoneNumber implements PhoneNumber {

  /**
   * The phone number making the call.
   */
	private String number;

	/**
   * Instantiates a new number only phone number.
   * 
   * @param number
   *          the number
   */
	public AbstractPhoneNumber(String number) {
		super();
		this.number = number;
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
   * Gets the phone number making the call.
   * 
   * @return the phone number making the call
   */
	public String getNumber() {
		return number;
	}
}
