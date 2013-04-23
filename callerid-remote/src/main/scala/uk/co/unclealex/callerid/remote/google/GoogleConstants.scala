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

/**
 * An interface for classes that contain the constants used to talk to Google. These constants are abstracted out
 * for testing purposes.
 */
case class GoogleConstants(
  /**
   * The URL used to get OAuth tokens from Google.
   */
  oauthTokenUrl: String = "https://accounts.google.com/o/oauth2/token",
  /**
   * The URL used to get contacts from Google.
   */
  contactFeedUrl: String = "https://www.google.com/m8/feeds/contacts/default/full",
  /**
   * The amount of time (in milliseconds) before expiry that an access token should be rerequested.
   */
  tokenExpiryTimeout: Long = 600000)