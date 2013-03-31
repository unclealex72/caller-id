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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * A persisted representation of an OAuth token that has been handed out by
 * Google for single sign on.
 */
@Entity
public class OauthToken {

  /**
   * The type of the token being persisted.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OauthTokenType tokenType;

  /**
   * The value of the token being persisted.
   */
  @Column(nullable = false)
  private String token;

  /**
   * The date and time that the token expires or null if the token does not
   * expire.
   */
  private Date expiryDate;

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    result = prime * result + ((tokenType == null) ? 0 : tokenType.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final OauthToken other = (OauthToken) obj;
    if (expiryDate == null) {
      if (other.expiryDate != null)
        return false;
    }
    else if (!expiryDate.equals(other.expiryDate))
      return false;
    if (token == null) {
      if (other.token != null)
        return false;
    }
    else if (!token.equals(other.token))
      return false;
    if (tokenType != other.tokenType)
      return false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "OauthToken [tokenType=" + tokenType + ", token=" + token + ", expiryDate=" + expiryDate + "]";
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
