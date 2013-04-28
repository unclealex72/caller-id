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
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers
import uk.co.unclealex.callerid.remote.contact.Contact
import uk.co.unclealex.callerid.remote.model.CallRecord
import uk.co.unclealex.callerid.remote.numbers.PhoneNumber
import uk.co.unclealex.callerid.remote.google.NowService
import uk.co.unclealex.callerid.remote.contact.ContactService
import uk.co.unclealex.callerid.remote.numbers.NumberLocationService
import uk.co.unclealex.callerid.remote.dao.CallRecordDao
/**
 * @author alex
 *
 */
class CallReceivedServiceImplTest extends FunSuite with ShouldMatchers with GivenWhenThen with MockFactory {

  test("Call received from a known contact") {
    execute("1", Some("Freddie Mercury"))
  }

  test("Call received from an unknown number") {
    execute("2", None)
  }

  def execute(phoneNumber: String, contactName: Option[String]) {
    val now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("28/04/2013 18:59:30")
    val nowService = stub[NowService]
    (nowService.now _).when().returns(now.getTime)
    val callRecordDao = stub[CallRecordDao]
    val numberLocationService = stub[NumberLocationService]
    (numberLocationService.decompose _).when("1").returns(PhoneNumber("+1", List(), None, "1"))
    (numberLocationService.decompose _).when("2").returns(PhoneNumber("+2", List(), None, "2"))
    val contactService = stub[ContactService]
    (contactService.getContactsByNormalisedPhoneNumber _).when().returns(Map("+1" -> Contact("Freddie Mercury", None)))
    When("a call has been received")
    Then("it should be persisted")
    val actualReceivedCall = new CallReceivedServiceImpl(
      nowService = nowService,
      numberLocationService = numberLocationService,
      contactService = contactService,
      callRecordDao = callRecordDao).callReceived(phoneNumber)
    val expectedCallRecord = new CallRecord
    expectedCallRecord.setTelephoneNumber("+" + phoneNumber)
    expectedCallRecord.setCallDate(now)
    (callRecordDao.store _).verify(expectedCallRecord)
    Then("the correct phone number information should be returned.")
    actualReceivedCall should equal(
      ReceivedCall(PhoneNumber("+" + phoneNumber, List(), None, phoneNumber), contactName))
  }
}