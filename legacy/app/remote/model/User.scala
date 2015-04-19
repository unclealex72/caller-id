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
package legacy.remote.model;

import org.squeryl.KeyedEntity
import org.squeryl.dsl.OneToMany
import org.squeryl.dsl.StatefulOneToMany
import java.util.Date
import java.sql.Timestamp

/**
 * A persitable person who can use the system.
 */
case class User(
  /**
   * The ID of the user
   */
  var id: Long,
  /**
   * The username of the user.
   */
  var username: String,
  /**
   * The short lived oauth access token from Google.
   */
  var accessToken: String,
  /**
   * The expiry date of the oauth accesss token.
   */
  var expiryDate: Timestamp,
  /**
   * The long lived oauth refresh token from Google.
   */
  var refreshToken: String) extends KeyedEntity[Long]

object User {

  def apply(username: String, accessToken: String, expiryDate: Date, refreshToken: String): User =
    new User(0, username, accessToken, new Timestamp(expiryDate.getTime()), refreshToken)
}
