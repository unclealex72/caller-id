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

import uk.co.unclealex.callerid.modem.listener.CallListener;

/**
 * An interface for classes that notify {@link CallListener}s of {@link Event}s.
 * @author alex
 *
 */
public interface EventNotifier {

  /**
   * Notify a list of {@link CallListener}s of an {@link Event}.
   * @param event The {@link Event} sent by the modem.
   * @param callListeners The {@link CallListener}s to be notified.
   */
  public void notify(Event event, Iterable<CallListener> callListeners);
}
