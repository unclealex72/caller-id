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

package uk.co.unclealex.callerid.google.model;

import java.util.Arrays;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A contact represents a single Google contact. Only their name and telephone
 * numbers are stored.
 * 
 * @author alex
 * 
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class Contact {

   /**
   * The contact's primary id.
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;

  /**
	 * The name of the contact.
	 */
  @Unique
	private String name;

	/**
	 * A list of telephone numbers associated with the contact.
	 */
	@Join
	@Element(dependent="true")
	private List<String> telephoneNumbers;

	
  /**
   * Create a new contact.
   * @param name The contact's name.
   * @param telephoneNumbers All telephone numbers associated with this contact.
   */
  public Contact(String name, List<String> telephoneNumbers) {
    super();
    this.name = name;
    this.telephoneNumbers = telephoneNumbers;
  }

  /**
   * Create a new contact.
   * @param name The contact's name.
   * @param telephoneNumbers All telephone numbers associated with this contact.
   */
  public Contact(String name, String... telephoneNumbers) {
    this(name, Arrays.asList(telephoneNumbers));
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
   * Gets the contact's primary id.
   * 
   * @return the contact's primary id
   */
  public Integer getId() {
    return id;
  }

	/**
   * Gets the name of the contact.
   * 
   * @return the name of the contact
   */
	public String getName() {
		return name;
	}
	
  /**
   * Gets the a list of telephone numbers associated with the contact.
   * 
   * @return the a list of telephone numbers associated with the contact
   */
  public List<String> getTelephoneNumbers() {
    return telephoneNumbers;
  }
}
