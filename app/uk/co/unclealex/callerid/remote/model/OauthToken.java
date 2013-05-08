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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A persisted representation of an OAuth token that has been handed out by
 * Google for single sign on.
 */
@Entity
public class OauthToken {

  /**
   * The ID of the OAuth token.
   */
  @Id
  @GeneratedValue
  private Integer id;

  /**
   * The type of the token, either REFRESH or ACCESS.
   */
  @Enumerated(EnumType.STRING)
  private OauthTokenType tokenType;

  /**
   * The token value received from Google, if any.
   */
  @Column
  private String token;

  /**
   * The expiry date of the token, if any.
   */
  @Column
  private Date expiryDate;

  /**
   * Instantiates a new oauth token.
   */
  protected OauthToken() {
    super();
  }

  /**
   * Instantiates a new oauth token.
   * 
   * @param tokenType
   *          the token type
   */
  public OauthToken(final OauthTokenType tokenType) {
    super();
    this.tokenType = tokenType;
  }

  public OauthToken(final OauthTokenType tokenType, final String token) {
    super();
    this.tokenType = tokenType;
    this.token = token;
  }

  public OauthToken(final OauthTokenType tokenType, final String token, final Date expiryDate) {
    super();
    this.tokenType = tokenType;
    this.token = token;
    this.expiryDate = expiryDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    if (id == null) {
      if (other.id != null)
        return false;
    }
    else if (!id.equals(other.id))
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
    return "OauthToken [id="
        + id
        + ", tokenType="
        + tokenType
        + ", token="
        + token
        + ", expiryDate="
        + expiryDate
        + "]";
  }

  /**
   * Gets the ID of the OAuth token.
   * 
   * @return the ID of the OAuth token
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the ID of the OAuth token.
   * 
   * @param id
   *          the new ID of the OAuth token
   */
  public void setId(final Integer id) {
    this.id = id;
  }

  /**
   * Gets the type of the token, either REFRESH or ACCESS.
   * 
   * @return the type of the token, either REFRESH or ACCESS
   */
  public OauthTokenType getTokenType() {
    return tokenType;
  }

  /**
   * Sets the type of the token, either REFRESH or ACCESS.
   * 
   * @param tokenType
   *          the new type of the token, either REFRESH or ACCESS
   */
  public void setTokenType(final OauthTokenType tokenType) {
    this.tokenType = tokenType;
  }

  /**
   * Gets the token value received from Google, if any.
   * 
   * @return the token value received from Google, if any
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets the token value received from Google, if any.
   * 
   * @param token
   *          the new token value received from Google, if any
   */
  public void setToken(final String token) {
    this.token = token;
  }

  /**
   * Gets the expiry date of the token, if any.
   * 
   * @return the expiry date of the token, if any
   */
  public Date getExpiryDate() {
    return expiryDate;
  }

  /**
   * Sets the expiry date of the token, if any.
   * 
   * @param expiryDate
   *          the new expiry date of the token, if any
   */
  public void setExpiryDate(final Date expiryDate) {
    this.expiryDate = expiryDate;
  }
}
