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
package legacy.remote.google

import legacy.remote.model.User

/**
 * An interface for retrieving OAuth access tokens from Google.
 */
trait GoogleTokenService {

  /**
   * Get the access token for a user, refreshing it if neccessary.
   * @param user The user who is requesting an access token.
   * @return An OAuth access token that will allow a user to access their Google contacts.
   */
  def accessToken(user: User): String

  /**
   * Find who has logged in and store their access and refresh tokens
   * @param successCode The success code supplied by Google.
   * @return A user with refresh and access tokens installed if none were known or None if the user was not allowed
   * to log in.
   */
  def userOf(successCode: String): Option[GoogleUser]

  /**
   * Get the Google OAuth page that should be redirected to for authorisation.
   */
  def loginPage: String
}
