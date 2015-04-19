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

import legacy.remote.number.PhoneNumber
import java.util.Date
import legacy.remote.contact.Contact

/**
 * A class that encapsulates a received call.
 * @author alex
 *
 */
case class ReceivedCall(
  /**
   * The date and time the call was received.
   */
  dateReceived: Date,
  /**
   * The number that made the call
   */
  phoneNumber: PhoneNumber,
  /**
   * The name of the contact associated with the phone number.
   */
  contact: Option[Contact]) {

}