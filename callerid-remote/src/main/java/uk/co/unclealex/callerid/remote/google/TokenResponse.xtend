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
package uk.co.unclealex.callerid.remote.google

import com.google.api.client.auth.oauth2.RefreshTokenRequest
import org.codehaus.jackson.annotate.JsonProperty
import org.codehaus.jackson.annotate.JsonCreator

/**
 * A JSON compatible class that encapsulate Google token responses.
 */
@Data class TokenResponse {
    
  /** Access token issued by the authorization server. */
  val String accessToken;

  /**
   * Token type (as specified in <a
   * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-23#section-7.1">Access Token Types</a>).
   */
  val String tokenType;

  /**
   * Lifetime in seconds of the access token (for example 3600 for an hour) or {@code null} for
   * none.
   */
  val Long expiresInSeconds;

  /**
   * Refresh token which can be used to obtain new access tokens using {@link RefreshTokenRequest}
   * or {@code null} for none.
   */
  val String refreshToken;

  /**
   * Scope of the access token as specified in <a
   * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-23#section-3.3">Access Token Scope</a> or
   * {@code null} for none.
   */
  val String scope;
    
  @JsonCreator
  new(@JsonProperty("access_token") String accessToken, @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") Long expiresInSeconds, @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("scope") String scope
  ) {
      this._accessToken = accessToken
      this._tokenType = tokenType
      this._expiresInSeconds = expiresInSeconds
      this._refreshToken = refreshToken
      this._scope = scope
  }
}