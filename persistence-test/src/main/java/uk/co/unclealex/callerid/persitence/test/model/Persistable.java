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

package uk.co.unclealex.callerid.persitence.test.model;

import java.util.Comparator;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A very simple object that is persistable.
 * 
 * @author alex
 * 
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class Persistable {

  public static Comparator<Persistable> COMPARATOR = new Comparator<Persistable>() {
    @Override
    public int compare(Persistable o1, Persistable o2) {
      return o1.getOrdering() - o2.getOrdering();
    }
  };
  
  /**
   * This object's primary key.
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;

  /**
   * The value that this bean holds.
   */
  private String value;

  /**
   * An ordering for testing.
   */
  private int ordering;
  
  /**
   * Default constructor for serialisation.
   */
  protected Persistable() {
    super();
  }
  
  /**
   * @param value
   */
  public Persistable(int ordering, String value) {
    super();
    this.ordering = ordering;
    this.value = value;
  }


  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
  
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
  
  /**
   * Gets the this object's primary key.
   * 
   * @return the this object's primary key
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the this object's primary key.
   * 
   * @param id
   *          the new this object's primary key
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets the value that this bean holds.
   * 
   * @return the value that this bean holds
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value that this bean holds.
   * 
   * @param value
   *          the new value that this bean holds
   */
  public void setValue(String value) {
    this.value = value;
  }

  public int getOrdering() {
    return ordering;
  }

  public void setOrdering(int ordering) {
    this.ordering = ordering;
  }
}
