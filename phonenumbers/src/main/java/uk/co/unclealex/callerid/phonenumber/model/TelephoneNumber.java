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

import java.util.Objects;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;

/**
 * A telephone number encapsulates the three parts of a telephone number: the
 * local number, the STD code and the international prefix.
 * 
 * @author alex
 * 
 */
@PersistenceCapable
@Unique(name="fullNumberIndex", members={"internationalPrefix", "stdCode", "number"})
public class TelephoneNumber {

	/**
	 * The telephone number's persistent ID.
	 */
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.NATIVE)
	private String id;
	
	/**
	 * The local part of a telephone number.
	 */
	@NotNull
	private String number;

	/**
	 * The telephone number's STD code.
	 */
	@NotNull
	private String stdCode;

	/**
	 * The telephone number's international prefix.
	 */
	@NotNull
	private String internationalPrefix;

	/**
	 * Default constructor for serlialisation.
	 */
	protected TelephoneNumber() {
		super();
	}

	/**
	 * Create a new telephone number.
	 * @param internationalPrefix The telephone number's international prefix.
	 * @param stdCode The telephone number's STD code.
	 * @param number The local part of the telephone number.
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
		return Objects.hash(getId(), getNumber(), getStdCode(), getInternationalPrefix());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TelephoneNumber
				&& Objects.equals(getId(), ((TelephoneNumber) obj).getId())
				&& Objects.equals(getNumber(), ((TelephoneNumber) obj).getNumber())
				&& Objects.equals(getStdCode(), ((TelephoneNumber) obj).getStdCode())
				&& Objects.equals(getInternationalPrefix(), ((TelephoneNumber) obj).getInternationalPrefix());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return com.google.common.base.Objects
				.toStringHelper(this)
				.add("id", getId())
				.add("internationalPrefix", getInternationalPrefix())
				.add("stdcode", getStdCode())
				.add("number", getNumber())
				.toString();
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

	/**
	 * Gets the telephone number's persistent ID.
	 * 
	 * @return the telephone number's persistent ID
	 */
	public String getId() {
		return id;
	}
}
