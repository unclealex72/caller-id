/**
 * Copyright 2012 Alex Jones
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

package uk.co.unclealex.callerid.calls.model

import java.util.Date
import java.util.List
import org.eclipse.xtend.lib.Data
import uk.co.unclealex.callerid.google.model.Contact
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber

/**
 * A model bean that contains all the known information about a received call. This can be either from a call
 * received from a modem or from a previously received call in the database.
 * @author alex
 * 
 */
@Data
public class ReceivedCall {

  /**
   * The instant the call was recevied.
   */
  Date startTime;

  /**
   * The {@link PhoneNumber} that made the call.
   */
  PhoneNumber phoneNumber;

  /**
   * The most recently manually attributed contact name to this number or null
   * if no name has been manually attributed.
   */
  String contactName;

  /**
   * A list of {@link Contact}s to who may be calling.
   */
  List<Contact> contacts;

}
