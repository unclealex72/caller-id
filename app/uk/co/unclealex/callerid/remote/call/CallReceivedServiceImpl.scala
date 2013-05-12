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

import java.util.Date
import uk.co.unclealex.callerid.remote.contact.ContactService
import uk.co.unclealex.callerid.remote.dao.CallRecordDao
import uk.co.unclealex.callerid.remote.google.NowService
import uk.co.unclealex.callerid.remote.model.CallRecord
import uk.co.unclealex.callerid.remote.number.NumberLocationService
import javax.inject.Inject

/**
 * The default implementation of {@link CallReceivedService}
 * @author alex
 *
 */
class CallReceivedServiceImpl @Inject() (
  /**
   * The contact service used to find who made a call.
   */
  contactService: ContactService,
  /**
   * The number location service used to create phone numbers.
   */
  numberLocationService: NumberLocationService,
  /**
   * The service used to get the current time.
   */
  nowService: NowService,
  /**
   * The call record DAO used to persist call records.
   */
  callRecordDao: CallRecordDao) extends CallReceivedService {

  override def callReceived(number: String): ReceivedCall = {
    val phoneNumber = numberLocationService.decompose(number)
    val normalisedNumber = phoneNumber.normalisedNumber
    val now = new Date(nowService.now)
    val callRecord = CallRecord(now, normalisedNumber)
    callRecordDao.store(callRecord)
    val contact =
      contactService.contactsByNormalisedPhoneNumber.get(normalisedNumber)
    ReceivedCall(now, phoneNumber, contact)
  }
}