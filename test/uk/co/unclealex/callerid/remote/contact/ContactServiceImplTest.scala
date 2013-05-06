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

package uk.co.unclealex.callerid.remote.contact

import scala.collection.JavaConversions.seqAsJavaList
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers
import uk.co.unclealex.callerid.remote.google.GoogleContact
import uk.co.unclealex.callerid.remote.google.GoogleContactsService
import uk.co.unclealex.callerid.remote.model.User
import uk.co.unclealex.callerid.remote.number.NumberLocationService
import uk.co.unclealex.callerid.remote.number.PhoneNumber
import uk.co.unclealex.callerid.remote.dao.UserDao
import scalaz.NonEmptyList
import uk.co.unclealex.callerid.remote.number.Country
/**
 * @author alex
 *
 */
class ContactServiceImplTest extends FunSuite with ShouldMatchers with GivenWhenThen with MockFactory {

  val countries = NonEmptyList(Country("UK", "44", "uk", List()))

  test("Get all contacts") {
    val numberLocationService = new NumberLocationService {
      override def decompose(number: String) = PhoneNumber("+" + number, countries, None, number)
    }
    val freddie = "freddie" asUser Set(GoogleContact("John", Some("Heathrow"), Set("1", "2")))
    val brian = "brian" asUser Set(
      GoogleContact("Roger", None, Set("3", "4")),
      GoogleContact("Spike", None, Set("5")))
    val users = List(freddie, brian)
    val googleContactsService = new GoogleContactsService {
      override def getAllContacts(user: User) = users.toMap.get(user).get
    }
    val userDao = stub[UserDao]
    (userDao.getAll _).when().returns(users.map { case (u, cs) => u })
    val contactService = new ContactServiceImpl(googleContactsService, numberLocationService, userDao)
    val actualContactsByPhoneNumber = contactService.getContactsByNormalisedPhoneNumber
    val expectedContactsByPhoneNumber = Map(
      "+1" -> Contact("John", Some("Heathrow")),
      "+2" -> Contact("John", Some("Heathrow")),
      "+3" -> Contact("Roger", None),
      "+4" -> Contact("Roger", None),
      "+5" -> Contact("Spike", None))
    actualContactsByPhoneNumber should equal(expectedContactsByPhoneNumber)
  }

  implicit class StringImplicits(str: String) {

    def asUser(googleContacts: Set[GoogleContact]) = {
      new User(str) -> googleContacts
    }
  }
}