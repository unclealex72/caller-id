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

import javax.inject.Inject
import legacy.remote.contact.ContactService
import legacy.remote.dao.CallRecordDao
import legacy.remote.number.NumberLocationService
import scala.collection.SortedSet
import scala.collection.JavaConversions._
import legacy.remote.model.CallRecord
import scala.collection.immutable.TreeSet
import legacy.remote.number.NumberLocationService
import java.util.Date

/**
 * The default implementation of {@link ReceivedCallsService}.
 * @author alex
 *
 */
class ReceivedCallsServiceImpl @Inject() (
  /**
   * The {@link CallRecordDao} used to get call records.
   */
  callRecordDao: CallRecordDao,
  /**
   * The {@link ContactService} used to get google contacts.
   */
  contactService: ContactService,
  /**
   * The {@link NumberLocationService} used to decompose phone numbers.
   */
  numberLocationService: NumberLocationService) extends ReceivedCallsService {

  def calls: SortedSet[ReceivedCall] = {
    val contactsByNormalisedPhoneNumber = contactService.contactsByNormalisedPhoneNumber
    val ordering = Ordering.by((rc: ReceivedCall) => rc.dateReceived).reverse
    val receivedCallFactory: CallRecord => ReceivedCall = { cr =>
      ReceivedCall(
        new Date(cr.callDate.getTime()),
        numberLocationService.decompose(cr.telephoneNumber),
        contactsByNormalisedPhoneNumber.get(cr.telephoneNumber))
    }
    SortedSet(callRecordDao.getAll.map(receivedCallFactory): _*)(ordering)
  }
}