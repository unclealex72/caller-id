/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance
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

import javax.inject.Inject
import legacy.remote.google.UrlWithParameters.UrlWithParametersStringImplicits
import legacy.remote.model.User
import com.typesafe.scalalogging.slf4j.Logging

/**
 * The default implementation of {@link GoogleContactsService}.
 */
class GoogleContactsServiceImpl @Inject() (
  /**
   * The service used to parse Google contacts.
   */
  googleContactsParser: GoogleContactsParser,
  /**
   * The service used to get tokens from Google.
   */
  googleTokenService: GoogleTokenService,
  /**
   * The Google constants object used to configure how to get contacts from Google.
   */
  googleConstants: GoogleConstants) extends GoogleContactsService {

  override def getAllContacts(user: User): Set[GoogleContact] = {
    val contactsUrl = googleConstants.contactFeedUrl withParameters (
      "access_token" -> googleTokenService.accessToken(user), "max-results" -> Int.MaxValue)
    googleContactsParser.parse(contactsUrl.toURL)
  }
}
