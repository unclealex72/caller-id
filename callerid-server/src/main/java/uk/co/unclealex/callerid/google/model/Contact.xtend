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

package uk.co.unclealex.callerid.google.model

import javax.jdo.annotations.Column
import javax.jdo.annotations.Element
import javax.jdo.annotations.IdGeneratorStrategy
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.Join
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.PrimaryKey
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.eclipse.xtend.lib.Property
import java.util.List

/**
 * A contact represents a single Google contact. Only their name and telephone
 * numbers are stored.
 * 
 * @author alex
 * 
 */
@PersistenceCapable(identityType = IdentityType::DATASTORE, detachable="true")
public class Contact {

   /**
   * The contact's primary id.
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy::NATIVE)
  @Column(name = "id")
  @Property Integer id;

  /**
	 * The name of the contact.
	 */
	 @Column(name = "name")
	@Property String name;

	/**
	 * A list of telephone numbers associated with the contact.
	 */
	@Join
	@Element(dependent="true")
	@Column(name = "telephoneNumbers")
	@Property List<String> telephoneNumbers;

	
  /**
   * Create a new contact.
   * @param name The contact's name.
   * @param telephoneNumbers All telephone numbers associated with this contact.
   */
  public new(String name, String... telephoneNumbers) {
    super();
    this.name = name;
    this.telephoneNumbers = telephoneNumbers;
  }

  /**
	 * {@inheritDoc}
	 */
	override equals(Object obj) {
	  EqualsBuilder::reflectionEquals(this, obj);
	}

	/**
	 * {@inheritDoc}
	 */
	override hashCode() {
	   HashCodeBuilder::reflectionHashCode(this);
	}

	/**
	 * {@inheritDoc}
	 */
	override toString() {
	  return ToStringBuilder::reflectionToString(this);
	}

}
