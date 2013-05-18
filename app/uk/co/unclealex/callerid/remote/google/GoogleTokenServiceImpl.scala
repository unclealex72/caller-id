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
 * @author unclealex72
 *
 */
package uk.co.unclealex.callerid.remote.google

import java.sql.Timestamp

import scala.collection.mutable.Map

import javax.inject.Inject
import uk.co.unclealex.callerid.remote.dao.UserDao
import uk.co.unclealex.callerid.remote.google.UrlWithParameters.UrlWithParametersStringImplicits
import uk.co.unclealex.callerid.remote.model.User
/**
 * The default implementation of {@link GoogleTokenService}.
 */
class GoogleTokenServiceImpl @Inject() (
  /**
   * The user DAO used to store an updated or new user.
   */
  userDao: UserDao,
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
  googleRequestService: GoogleRequestService,
  /**
   * The service used to authorise Google users.
   */
  googleAuthorisationService: GoogleAuthorisationService) extends GoogleTokenService {

  /**
   * Update a user's access token.
   * @param user The user whose access token needs updating.
   * @param accessToken The user's current access token.
   */
  def updateAccessToken(user: User): Unit = {
    val newAccessToken = requestToken("refresh_token", user.refreshToken, "refresh_token", false)
    user.accessToken = newAccessToken.accessToken
    user.expiryDate = expiryDate(newAccessToken).getOrElse(
      throw new IllegalStateException("No expiry date was found for an access token."))
    userDao store user
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
      parameters += "redirect_uri" -> googleConfiguration.callbackUrl
    }
    googleRequestService.sendTokenPostRequest(googleConstants.oauthTokenUrl, parameters)
  }

  /**
   * Get the user's access token, refreshing it if neccessary.
   * @param user The user whose token is being looked for.
   * @param A valid access token.
   */
  def accessToken(user: User): String = {
    if (expired(user)) {
      updateAccessToken(user)
    }
    user.accessToken
  }

  /**
   * Calculate the expiry date of a Google {@link TokenResponse}.
   * @param tokenResponse the token response from Google.
   * @return The date and time the token expires.
   */
  def expiryDate(tokenResponse: TokenResponse): Option[Timestamp] =
    tokenResponse.expiresInSeconds.map(secs => new Timestamp(secs * 1000L + nowService.now))

  /**
   * Determine whether an access token has expired.
   * @param oauthToken The token to check.
   * @return True if the token's expiry date is soon to expire, expired or non existent, false otherwise.
   */
  def expired(user: User): Boolean = {
    user.expiryDate.getTime - googleConstants.tokenExpiryTimeout < nowService.now
  }

  override def userOf(successCode: String): Option[GoogleUser] = {
    val token = requestToken("code", successCode, "authorization_code", true)
    val userInfo =
      googleRequestService.sendProfileGetRequest(googleConstants.userProfileUrl.withParameters("access_token" -> token.accessToken))
    val googleUser = GoogleUser(userInfo.email, userInfo.name)
    if (googleAuthorisationService authorised googleUser) {
      val existingUser = userDao findByEmailAddress googleUser.email
      if (existingUser.isEmpty) {
        val user = User(
          googleUser.email,
          token.accessToken,
          expiryDate(token).getOrElse(
            throw new IllegalStateException("No expiry date was supplied by Google.")),
          token.refreshToken.getOrElse(
            throw new IllegalStateException("No refresh token was supplied by Google.")))
        userDao store user
      }
      Some(googleUser)
    } else {
      None
    }
  }

  override def loginPage: String = {
    googleConstants.loginUrl.withParameters(
      "scope" -> googleConstants.scopes.mkString(" "),
      "response_type" -> "code",
      "access_type" -> "offline",
      "client_id" -> googleConfiguration.consumerId,
      "redirect_uri" -> googleConfiguration.callbackUrl).toURL.toString()
  }

}
