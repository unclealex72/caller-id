/**
 * Copyright 2013 Alex Jones
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
package uk.co.unclealex.callerid.remote.model;

import java.util.Date;

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
 * A persisted representation of an OAuth token that has been handed out by
 * Google for single sign on.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, table = "tokens", detachable = "true")
public class OauthToken {

  /** The id. */
  @PrimaryKey(name = "id")
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;

  /**
   * The type of the token being persisted.
   */
  @NotNull
  @Column(name = "tokenType")
  private OauthTokenType tokenType;

  /**
   * The value of the token being persisted.
   */
  @NotNull
  @Column(name = "token")
  private String token;

  /**
   * The date and time that the token expires or null if the token does not
   * expire.
   */
  @Column(name = "expiryDate")
  private Date expiryDate;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
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
   * Gets the id.
   * 
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the id.
   * 
   * @param id
   *          the new id
   */
  public void setId(final Integer id) {
    this.id = id;
  }

  /**
   * Gets the type of the token being persisted.
   * 
   * @return the type of the token being persisted
   */
  public OauthTokenType getTokenType() {
    return tokenType;
  }

  /**
   * Sets the type of the token being persisted.
   * 
   * @param tokenType
   *          the new type of the token being persisted
   */
  public void setTokenType(final OauthTokenType tokenType) {
    this.tokenType = tokenType;
  }

  /**
   * Gets the value of the token being persisted.
   * 
   * @return the value of the token being persisted
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets the value of the token being persisted.
   * 
   * @param token
   *          the new value of the token being persisted
   */
  public void setToken(final String token) {
    this.token = token;
  }

  /**
   * Gets the date and time that the token expires or null if the token does not
   * expire.
   * 
   * @return the date and time that the token expires or null if the token does
   *         not expire
   */
  public Date getExpiryDate() {
    return expiryDate;
  }

  /**
   * Sets the date and time that the token expires or null if the token does not
   * expire.
   * 
   * @param expiryDate
   *          the new date and time that the token expires or null if the token
   *          does not expire
   */
  public void setExpiryDate(final Date expiryDate) {
    this.expiryDate = expiryDate;
  }

}