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

package uk.co.unclealex.callerid.modem.listener;

import uk.co.unclealex.callerid.calls.model.ReceivedCall;

/**
 * An interface for classes that can listen to an event on a modem. Two events
 * can be handled: the initial ring and the broadcast of the number that is
 * calling.
 * 
 * @author alex
 * 
 */
public interface CallListener {

  /**
   * Respond to the telephone ringing.
   * 
   * @return True if other {@link CallListener}s should be notified of the call
   *         or false if no other listeners should be made aware of the call.
   * @throws Exception
   */
  public boolean onRing() throws Exception;

  /**
   * Respond to the telephone announcing its caller id information.
   * 
   * @param receivedCall
   *          The call that was received.
   * @return True if other {@link CallListener}s should be notified of the call
   *         or false if no other listeners should be made aware of the call.
   * @throws Exception
   */
  public boolean onNumber(ReceivedCall receivedCall) throws Exception;
}
