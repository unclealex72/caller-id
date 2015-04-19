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
package legacy.remote.google

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSpec
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import legacy.remote.model.User
import java.util.ArrayList
import java.net.URL
import java.util.Date

/**
 * Test that, given a predefined set of contacts, they are correctly read by the Google Contacts Service.
 */
class GoogleContactsServiceImplTest extends FunSuite with ShouldMatchers with MockFactory {

  test("Get contacts") {
    val googleConstants = new GoogleConstants
    val googleTokenService = stub[GoogleTokenService]
    val googleContactsParser = stub[GoogleContactsParser]
    val googleContacts = Set(
      new GoogleContact("Brian May", Some("House"), Set("+44111222")),
      new GoogleContact("Freddie Mercury", None, Set("+44222333")))
    val user = User("username", "access", new Date(), "refresh")
    (googleTokenService.accessToken _).when(user).returns("access")
    (googleContactsParser.parse _).when(
      new URL("https://www.google.com/m8/feeds/contacts/default/full?access_token=access&max-results=2147483647")).
      returns(googleContacts)
    val googleContactsService =
      new GoogleContactsServiceImpl(googleContactsParser, googleTokenService, googleConstants)
    googleContactsService.getAllContacts(user) should equal(googleContacts)
  }

}