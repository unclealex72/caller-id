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

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A persitable person who can use the system.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, table = "users", detachable = "true")
public class User {

  /** The id. */
  @PrimaryKey(name = "id")
  @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
  private Integer id;

  /**
   * The user's Google username.
   */
  @NotNull
  @Unique
  @Column(name = "username")
  private String username;

  /**
   * The OAuth tokens given by Google single sign on.
   */
  @Persistent
  @Element(dependent = "true")
  private List<OauthToken> oauthTokens;

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
   * Gets the user's Google username.
   * 
   * @return the user's Google username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the user's Google username.
   * 
   * @param username
   *          the new user's Google username
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * Gets the OAuth tokens given by Google single sign on.
   * 
   * @return the OAuth tokens given by Google single sign on
   */
  public List<OauthToken> getOauthTokens() {
    return oauthTokens;
  }

  /**
   * Sets the OAuth tokens given by Google single sign on.
   * 
   * @param oauthTokens
   *          the new OAuth tokens given by Google single sign on
   */
  public void setOauthTokens(final List<OauthToken> oauthTokens) {
    this.oauthTokens = oauthTokens;
  }
}
