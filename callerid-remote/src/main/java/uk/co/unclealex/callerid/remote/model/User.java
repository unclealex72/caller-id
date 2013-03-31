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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * A persitable person who can use the system.
 */
@Entity
public class User {

  /**
   * The user's Google username.
   */
  @Column(nullable = false, unique = true)
  private String username;

  /**
   * The OAuth tokens given by Google single sign on.
   */
  @OneToMany(cascade = CascadeType.ALL)
  private List<OauthToken> oauthTokens;

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
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
    return "User [username=" + username + ", oauthTokens=" + oauthTokens + "]";
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
