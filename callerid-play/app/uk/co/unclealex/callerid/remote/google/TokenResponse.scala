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

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A JSON compatible class that encapsulate Google token responses.
 */
case class TokenResponse(
  /** The Access token issued by the authorization server. */
  @JsonProperty("access_token") accessToken: String,
  /**
   * Token type (as specified in <a
   * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-23#section-7.1">Access Token Types</a>).
   */
  @JsonProperty("token_type") tokenType: String,
  /**
   * Lifetime in seconds of the access token (for example 3600 for an hour).
   */
  @JsonProperty("expires_in") expiresInSeconds: Option[Int],
  /**
   * Refresh token which can be used to obtain new access tokens using {@link RefreshTokenRequest}.
   */
  @JsonProperty("refresh_token") refreshToken: Option[String],
  /**
   * Scope of the access token as specified in <a
   * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-23#section-3.3">Access Token Scope</a>.
   */
  @JsonProperty("scope") scope: Option[String])

