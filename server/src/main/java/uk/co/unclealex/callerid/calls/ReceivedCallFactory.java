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

package uk.co.unclealex.callerid.calls;

import java.util.Date;

import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;

/**
 * An interface for classes that produce {@link ReceivedCall}s from either
 * {@link Call}s or from a current ringing call.
 * 
 * @author alex
 * 
 */
public interface ReceivedCallFactory {

  /**
   * Create a {@link ReceivedCall} from a current ringing call.
   * 
   * @param receivedCallTime
   *          The current time.
   * @param callingNumber
   *          The number that is calling as identified by the modem.
   * @return A {@link ReceivedCall} containing all known information about the
   *         call.
   */
  public ReceivedCall create(Date receivedCallTime, String callingNumber);
}
