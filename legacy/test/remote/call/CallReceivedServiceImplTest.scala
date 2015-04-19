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

package legacy.remote.call

import java.text.SimpleDateFormat
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers
import legacy.remote.contact.Contact
import legacy.remote.model.CallRecord
import legacy.remote.number.PhoneNumber
import legacy.remote.google.NowService
import legacy.remote.contact.ContactService
import legacy.remote.number.NumberLocationService
import legacy.remote.dao.CallRecordDao
import scalaz.NonEmptyList
import legacy.remote.number.Country
import org.scalamock.FunctionAdapter1
import java.sql.Timestamp
import java.util.Date
/**
 * @author alex
 *
 */
class CallReceivedServiceImplTest extends FunSuite with ShouldMatchers with GivenWhenThen with MockFactory {

  val countries = NonEmptyList(Country("UK", "44", "uk", List()))
  test("Call received from a known contact") {
    execute("1", Some(Contact("Freddie Mercury", None)))
  }

  test("Call received from an unknown number") {
    execute("2", None)
  }

  def execute(phoneNumber: String, contact: Option[Contact]) {
    val now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("28/04/2013 18:59:30").getTime
    val nowService = stub[NowService]
    (nowService.now _).when().returns(now)
    val callRecordDao = stub[CallRecordDao]
    val numberLocationService = stub[NumberLocationService]
    (numberLocationService.decompose _).when(phoneNumber).returns(
      PhoneNumber(s"+$phoneNumber", countries, None, phoneNumber))
    val contactService = stub[ContactService]
    (contactService.contactsByNormalisedPhoneNumber _).when().returns(Map("+1" -> Contact("Freddie Mercury", None)))
    When("a call has been received")
    Then("it should be persisted")
    val actualReceivedCall = new CallReceivedServiceImpl(
      nowService = nowService,
      numberLocationService = numberLocationService,
      contactService = contactService,
      callRecordDao = callRecordDao).callReceived(phoneNumber)
    (callRecordDao store _).verify(
      new FunctionAdapter1((cr: CallRecord) => cr.callDate == new Timestamp(now) && cr.telephoneNumber == s"+$phoneNumber"))
    Then("the correct phone number information should be returned.")
    actualReceivedCall should equal(
      ReceivedCall(new Date(now), PhoneNumber("+" + phoneNumber, countries, None, phoneNumber), contact))
  }
}