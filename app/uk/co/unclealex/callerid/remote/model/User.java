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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 * A persitable person who can use the system.
 */
@Entity
public class User {

  /**
   * The ID of the user.
   */
  @Id
  @GeneratedValue
  private Integer id;

  /**
   * The username of the user.
   */
  @Column
  @NotNull
  private String username;

  /**
   * The user's oauth tokens.
   */
  @OneToMany
  private List<OauthToken> oauthTokens = new ArrayList<>();

  /**
   * Instantiates a new user.
   */
  protected User() {
    super();
  }

  /**
   * Instantiates a new user.
   * 
   * @param username
   *          the username
   */
  public User(final String username) {
    this.username = username;
  }

  /**
   * Instantiates a new user.
   * 
   * @param username
   *          the username
   * @param oauthTokens
   *          the oauth tokens
   */
  public User(final String username, final List<OauthToken> oauthTokens) {
    this.username = username;
    this.oauthTokens = oauthTokens;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((oauthTokens == null) ? 0 : oauthTokens.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
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
    final User other = (User) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    }
    else if (!id.equals(other.id))
      return false;
    if (oauthTokens == null) {
      if (other.oauthTokens != null)
        return false;
    }
    else if (!oauthTokens.equals(other.oauthTokens))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    }
    else if (!username.equals(other.username))
      return false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "User [id=" + id + ", username=" + username + ", oauthTokens=" + oauthTokens + "]";
  }

  /**
   * Gets the ID of the user.
   * 
   * @return the ID of the user
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the ID of the user.
   * 
   * @param id
   *          the new ID of the user
   */
  public void setId(final Integer id) {
    this.id = id;
  }

  /**
   * Gets the username of the user.
   * 
   * @return the username of the user
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user.
   * 
   * @param username
   *          the new username of the user
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * Gets the user's oauth tokens.
   * 
   * @return the user's oauth tokens
   */
  public List<OauthToken> getOauthTokens() {
    return oauthTokens;
  }

  /**
   * Sets the user's oauth tokens.
   * 
   * @param oauthTokens
   *          the new user's oauth tokens
   */
  public void setOauthTokens(final List<OauthToken> oauthTokens) {
    this.oauthTokens = oauthTokens;
  }

}
