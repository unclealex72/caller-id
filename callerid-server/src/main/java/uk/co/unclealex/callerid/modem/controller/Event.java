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

package uk.co.unclealex.callerid.modem.controller;

import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.modem.listener.CallListener;

/**
 * An interface that encapsulates a {@link CallListener} responding to an event.
 * 
 * @author alex
 * 
 */
public interface Event {

  /**
   * Run the event.
   * 
   * @param callListener
   *          The {@link CallListener} to delegate to.
   * @return True if further events should be called, false otherwise.
   * @throws Exception
   *           Thrown if there are any problems.
   */
  public boolean onEvent(CallListener callListener) throws Exception;

  /**
   * An {@link Event} for the {@link CallListener#onRing()} method.
   * 
   * @author alex
   * 
   */
  public class OnRing implements Event {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onEvent(CallListener callListener) throws Exception {
      return callListener.onRing();
    }
  }

  /**
   * An {@link Event} for the {@link CallListener#onNumber(String)} method.
   * 
   * @author alex
   * 
   */
  public class OnNumber implements Event {

    /**
     * The call that was received.
     */
    private final ReceivedCall receivedCall;

    
    /**
     * Instantiates a new on number.
     * 
     * @param receivedCall
     *          the received call
     */
    public OnNumber(ReceivedCall receivedCall) {
      super();
      this.receivedCall = receivedCall;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onEvent(CallListener callListener) throws Exception {
      return callListener.onNumber(getReceivedCall());
    }

    /**
     * Gets the call that was received.
     * 
     * @return the call that was received
     */
    public ReceivedCall getReceivedCall() {
      return receivedCall;
    }
  }

}
