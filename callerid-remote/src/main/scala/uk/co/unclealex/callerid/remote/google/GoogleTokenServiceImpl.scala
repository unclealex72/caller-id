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

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.mutable.Map
import uk.co.unclealex.callerid.remote.model.OauthToken
import uk.co.unclealex.callerid.remote.model.OauthTokenType
import uk.co.unclealex.callerid.remote.model.User
import scala.collection.mutable.Buffer
import java.util.ArrayList

/**
 * The default implementation of {@link GoogleTokenService}.
 */
class GoogleTokenServiceImpl(

  /**
   * The Google configuration object used to configure how to get contacts from Google.
   */
  googleConfiguration: GoogleConfiguration,

  /**
   * The Google constants object used to configure how to get contacts from Google.
   */
  googleConstants: GoogleConstants,

  /**
   * The service used to get the current time.
   */
  nowService: NowService,

  /**
   * The service used to send requests to Google on the wire.
   */
  googleRequestService: GoogleRequestService) extends GoogleTokenService {

  /**
   * Implicits for Users and their tokens.
   */
  implicit class UserTokenImplicits(user: User) {
    /**
     * Find a user's token by it's token type.
     * @param user The user to interrogate.
     * @param oauthTokenType the token type to search for.
     * @return A token of the given type.
     */
    def findTokenByType(oauthTokenType: OauthTokenType): Option[OauthToken] =
      user.getOauthTokens().find(token => token.getTokenType() == oauthTokenType)
    /**
     * Find or create a user's token by it's token type.
     * @param user The user to interrogate.
     * @param oauthTokenType the token type to search for.
     * @return A token of the given type or a new token with the given type if none such exists.
     */
    def findTokenByTypeOrCreate(tokenType: OauthTokenType): OauthToken = {
      user.findTokenByType(tokenType).getOrElse({
        val oauthToken = new OauthToken
        oauthToken.setTokenType(tokenType)
        user.getOauthTokens() += oauthToken
        oauthToken
      })
    }

    /**
     * Update a user's access token.
     * @param user The user whose access token needs updating.
     * @param accessToken The user's current access token.
     */
    def updateAccessToken(userAccessToken: OauthToken): Unit = {
      val refreshToken: Option[OauthToken] = user.findTokenByType(OauthTokenType.REFRESH)
      refreshToken.map { refreshToken =>
        val newAccessToken = requestToken("refresh_token", refreshToken.getToken(), "refresh_token", false)
        userAccessToken.setToken(newAccessToken.accessToken)
        userAccessToken.setExpiryDate(expiryDate(newAccessToken).orNull)
      }.getOrElse(throw new GoogleAuthenticationFailedException("No refresh token found."))
    }
  }

  /**
   * Request a an OAuth token from Google.
   * @param tokenType The type of token to request.
   * @param token The user's current refresh token.
   * @param grantType The grant_type to send to Google with this request.
   * @param includeRedirect True if a redirect URI should be included in the request, false otherwise.
   * @return The {@link TokenResponse} received from Google.
   */
  def requestToken(tokenType: String, token: String, grantType: String, includeRedirect: Boolean): TokenResponse = {
    val parameters: Map[String, String] = Map(
      "client_id" -> googleConfiguration.consumerId,
      "client_secret" -> googleConfiguration.consumerSecret,
      tokenType -> token,
      "grant_type" -> grantType)
    if (includeRedirect) {
      parameters += "redirect_uri" -> "urn:ietf:wg:oauth:2.0:oob"
    }
    googleRequestService.sendRequest(classOf[TokenResponse], googleConstants.oauthTokenUrl, parameters)
  }

  /**
   * Get the user's access token, refreshing it if neccessary.
   * @param user The user whose token is being looked for.
   * @param A valid access token.
   */
  def accessToken(user: User): String = {
    val optionalAccessToken: Option[OauthToken] = user.findTokenByType(OauthTokenType.ACCESS)
    val accessToken: OauthToken = optionalAccessToken.getOrElse({
      val token = new OauthToken
      token.setTokenType(OauthTokenType.ACCESS)
      user.getOauthTokens().add(token)
      token
    })
    if (expired(accessToken)) {
      user.updateAccessToken(accessToken)
    }
    accessToken.getToken()
  }

  /**
   * Calculate the expiry date of a Google {@link TokenResponse}.
   * @param tokenResponse the token response from Google.
   * @return The date and time the token expires.
   */
  def expiryDate(tokenResponse: TokenResponse): Option[Date] =
    tokenResponse.expiresInSeconds.map(secs => new Date(secs * 1000 + nowService.now))

  /**
   * Determine whether an access token has expired.
   * @param oauthToken The token to check.
   * @return True if the token's expiry date is soon to expire, expired or non existent, false otherwise.
   */
  def expired(oauthToken: OauthToken): Boolean = {
    val expiryDate = oauthToken.getExpiryDate()
    expiryDate == null || (expiryDate.getTime() - googleConstants.tokenExpiryTimeout) < nowService.now
  }

  override def installSuccessCode(user: User, successCode: String) {
    val userAccessToken = user.findTokenByTypeOrCreate(OauthTokenType.ACCESS)
    val userRefreshToken = user.findTokenByTypeOrCreate(OauthTokenType.REFRESH)
    val token = requestToken("code", successCode, "authorization_code", true)
    userAccessToken.setToken(token.accessToken)
    userAccessToken.setExpiryDate(expiryDate(token).orNull)
    userRefreshToken.setToken(token.refreshToken.orNull)
  }
}
