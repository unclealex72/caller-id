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

package uk.co.unclealex.callerid.calls.listener;

import uk.co.unclealex.callerid.calls.model.ReceivedCall;

/**
 * An interface for classes that respond to a telephone call being received.
 * Each listener only gets given the <i>exact</i> recieved number (or null if
 * the number was withheld). This means that implementations will be responsible
 * for interpreting this number as they see fit.
 * 
 * @author alex
 * 
 */
public interface ReceivedCallListener {

  /**
   * Act upon a received call.
   * 
   * @param receivedCall
   *          The {@link ReceivedCall} that has just been received.
   */
  public void onCallReceived(ReceivedCall receivedCall) throws Exception;
}
