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
 * @author unclealex72
 *
 */

package uk.co.unclealex.callerid.google.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

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
   * A {@link Comparator} that can be used to sort contacts by their name.
   */
  public static Comparator<Contact> NAME_COMPARATOR = new Comparator<Contact>() {
    @Override
    public int compare(Contact o1, Contact o2) {
      return o1.getName().compareTo(o2.getName());
    }
  };
  
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
	@Persistent
	private List<TelephoneNumber> telephoneNumbers;

	/**
	 * Default constructor for serialisation.
	 */
	protected Contact() {
		super();
	}

	/**
	 * Instantiates a new contact.
	 *
	 * @param name the name
	 * @param telephoneNumbers the telephone numbers
	 */
	public Contact(String name, List<TelephoneNumber> telephoneNumbers) {
		super();
		this.name = name;
		this.telephoneNumbers = telephoneNumbers;
	}

	/**
	 * Instantiates a new contact.
	 *
	 * @param name the name
	 * @param telephoneNumbers the telephone numbers
	 */
	public Contact(String name, TelephoneNumber... telephoneNumbers) {
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the telephone numbers.
	 *
	 * @return the telephone numbers
	 */
	public List<TelephoneNumber> getTelephoneNumbers() {
		return telephoneNumbers;
	}

  /**
   * Gets the id.
   *
   * @return the id
   */
  public Integer getId() {
    return id;
  }
}
