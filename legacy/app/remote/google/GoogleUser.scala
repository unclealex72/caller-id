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

/**
 * A class that encapsulates a logged in user from Google.
 * @author alex
 *
 */
case class GoogleUser(
  /**
   * The user's Google email.
   */
  email: String,
  /**
   * The name of the user taken from their user profile.
   */
  name: String) {

  def serialise: String = s"$email:$name"
}

/**
 * The companion object for a Google user.
 */
object GoogleUser {

  /**
   * Decompose a string containing a user's email and name, separated by a colon. Both must be non-empty.
   */
  def unapply(user: String): Option[(String, String)] = {
    val userParts = user.split(':')
    if (userParts.size == 2 && !(userParts exists { _.isEmpty() })) Some(userParts(0), userParts(1)) else None
  }

  /**
   * Parse a string containing a user's email and name, separated by a colon.
   */
  def parse(str: String): Option[GoogleUser] = {
    str match {
      case GoogleUser(email, name) => Some(GoogleUser(email, name))
      case _ => None
    }
  }
}
