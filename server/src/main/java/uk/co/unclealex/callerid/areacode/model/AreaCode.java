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

package uk.co.unclealex.callerid.areacode.model;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A class that encapsulates all of the information about telephone area codes
 * and the areas they represent.
 * 
 * @author alex
 * 
 */
@PersistenceCapable(table = "AreaCodes",
    identityType=IdentityType.DATASTORE)
public class AreaCode {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;
  
  /**
   * The country of origin for this area code.
   */
  @Column(name = "country")
  @NotNull
  private String country;

  /**
   * The country code for this area code.
   */
  @Column(name = "countrycode")
  @NotNull
  private String countryCode;

  /**
   * The town or city this area code represents.
   */
  @Column(name = "area")
  private String area;

  /**
   * The area code itself. This field can contain spurious characters at the
   * beginning so only digits from the end of the string should be used.
   */
  @Column(name = "areacode")
  private String areaCode;

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
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * Gets the country of origin for this area code.
   * 
   * @return the country of origin for this area code
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the country of origin for this area code.
   * 
   * @param country
   *          the new country of origin for this area code
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Gets the country code for this area code.
   * 
   * @return the country code for this area code
   */
  public String getCountryCode() {
    return countryCode;
  }

  /**
   * Sets the country code for this area code.
   * 
   * @param countryCode
   *          the new country code for this area code
   */
  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  /**
   * Gets the town or city this area code represents.
   * 
   * @return the town or city this area code represents
   */
  public String getArea() {
    return area;
  }

  /**
   * Sets the town or city this area code represents.
   * 
   * @param area
   *          the new town or city this area code represents
   */
  public void setArea(String area) {
    this.area = area;
  }

  /**
   * Gets the area code itself.
   * 
   * @return the area code itself
   */
  public String getAreaCode() {
    return areaCode;
  }

  /**
   * Sets the area code itself.
   * 
   * @param areaCode
   *          the new area code itself
   */
  public void setAreaCode(String areaCode) {
    this.areaCode = areaCode;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
