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

package uk.co.unclealex.callerid.calls.model

import javax.jdo.annotations.Column
import javax.jdo.annotations.IdGeneratorStrategy
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.PrimaryKey
import javax.jdo.annotations.Unique
import javax.validation.constraints.NotNull
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.eclipse.xtend.lib.Property
import java.util.Date

/**
 * A class that models received telephone calls.
 * 
 * @author alex
 */
@PersistenceCapable(identityType = IdentityType::DATASTORE, detachable="true", table="calls")
public class Call {

  /**
   * The call's primary id.
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy::NATIVE)
  @Column(name = "id")
  @Property Integer id;

  /**
   * The time at which this call was received.
   */
  @NotNull
  @Unique
  @Persistent(defaultFetchGroup="true")
  @Column(name = "callTime")
  @Property Date callTime;
  
  /**
   * The telephone number who called or null if the number was withheld.
   */
  @Column(name = "telephoneNumber")
  @Property String telephoneNumber;
  
  /**
   * The name of the contact associated with this number. This may be derived from the known list of contacts or user entered.
   */
  @Column(name = "contactName")
  @Property String contactName;

  /**
   * Instantiates a new call.
   * 
   * @param callTime
   *          the call time
   * @param telephoneNumber
   *          the telephone number
   * @param contactName
   *          the contact name
   */
  new(Date callTime, String telephoneNumber, String contactName) {
    super();
    this.callTime = callTime;
    this.telephoneNumber = telephoneNumber;
    this.contactName = contactName;
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
    ToStringBuilder::reflectionToString(this);
  }

}
