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

import scala.annotation.migration

import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

/**
 * @author alex
 *
 */
class GoogleContactsParserImplTest extends FunSuite with ShouldMatchers with GivenWhenThen {

  test("Parsing Google contacts") {
    val expectedContacts = List(
      GoogleContact("Brian May", None, Set("+441256999888", "+447808123456")),
      GoogleContact("Freddie Mercury", None, Set("+447535654321")),
      GoogleContact("John Deacon", Some("46 The High Street, Basingstoke RG24 8NA"), Set("+44 7818 555444")),
      GoogleContact("Roger Taylor", Some("38 Dental Street, Brighton Hill, Basingstoke, RG22 7NA"), Set("07941212121")))
    When("retrieving the members of Queen from Google")
    val actualGoogleContacts =
      new GoogleContactsParserImpl().parse(getClass().getClassLoader().getResource("contacts.xml"))
    Then(s"there should be ${expectedContacts.length} members.")
    actualGoogleContacts should have size (expectedContacts.length)
    val actualGoogleContactsByName = actualGoogleContacts.map(c => c.name -> c).toMap
    expectedContacts.foreach { expectedContact =>
      Then(s"${expectedContact.name} should be a member of Queen")
      val matchingContact = actualGoogleContactsByName.get(expectedContact.name)
      matchingContact should not equal (None)
      matchingContact.map { actualContact =>
        Then(s"${expectedContact.name} should have the correct phone numbers.")
        actualContact.telephoneNumbers should equal(expectedContact.telephoneNumbers)
        Then(s"${expectedContact.name} should have the correct address.")
        actualContact.address should equal(expectedContact.address)
      }
    }
  }
}
