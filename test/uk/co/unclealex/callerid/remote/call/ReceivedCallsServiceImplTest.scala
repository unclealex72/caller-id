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

package uk.co.unclealex.callerid.remote.call

import java.text.SimpleDateFormat
import java.util.Date
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import uk.co.unclealex.callerid.remote.contact.Contact
import uk.co.unclealex.callerid.remote.model.CallRecord
import uk.co.unclealex.callerid.remote.number.City
import uk.co.unclealex.callerid.remote.number.Country
import uk.co.unclealex.callerid.remote.number.PhoneNumber
import uk.co.unclealex.callerid.remote.contact.ContactService
import uk.co.unclealex.callerid.remote.number.NumberLocationService
import uk.co.unclealex.callerid.remote.dao.CallRecordDao
import scalaz.NonEmptyList
import org.scalatest.matchers.ShouldMatchers

/**
 * @author alex
 *
 */
class ReceivedCallsServiceImplTest extends FunSuite with ShouldMatchers with GivenWhenThen with MockFactory {

  val uk = NonEmptyList(Country("UK", "44", "uk", List()))
  val basingstoke = Some(City("Basingstoke", "1256"))

  test("Call records are transformed into received calls") {
    // Set up the call record dao
    val callRecordDao = stub[CallRecordDao]
    val callRecords =
      List(0 -> "+441256769123", 5 -> "+441256769124", 10 -> "+441256769125").map {
        case (minutes, number) => new CallRecord(minutes later, number)
      }
    (callRecordDao.getAll _).when().returns(callRecords)

    // Set up the number location service
    val numberLocationService = stub[NumberLocationService]
    List("+441256769123" -> "769123", "+441256769124" -> "769124", "+441256769125" -> "769125").foreach {
      case (normalised, number) =>
        (numberLocationService.decompose _).when(normalised).returns(PhoneNumber(normalised, uk, basingstoke, number))
    }

    // And finally the contact service
    val contactService = stub[ContactService]
    (contactService.contactsByNormalisedPhoneNumber _).when().returns(Map("+441256769124" -> Contact("Brian May", None)))

    val expectedReceivedCalls = List(
      ReceivedCall(10 later, PhoneNumber("+441256769125", uk, basingstoke, "769125"), None),
      ReceivedCall(5 later, PhoneNumber("+441256769124", uk, basingstoke, "769124"), Some(Contact("Brian May", None))),
      ReceivedCall(0 later, PhoneNumber("+441256769123", uk, basingstoke, "769123"), None))
    When("getting a list of received calls")
    Then("the correct calls should be received")
    val receivedCallService = new ReceivedCallsServiceImpl(callRecordDao, contactService, numberLocationService)
    receivedCallService.calls.toList should equal(expectedReceivedCalls)
  }

  implicit class TimeImplicits(minutes: Int) {
    val datum = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("05/09/2013 09:12:00").getTime

    def later = {
      new Date(datum + minutes * 60 * 1000)
    }
  }
}