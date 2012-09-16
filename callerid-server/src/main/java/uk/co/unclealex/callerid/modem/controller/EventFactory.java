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

import uk.co.unclealex.callerid.modem.Modem;

/**
 * An interface for classes that can translate a string from a {@link Modem}
 * into an {@link Event}.
 * 
 * @author alex
 * 
 */
public interface EventFactory {

  /**
   * Create an {@link Event} from a line received from a {@link Modem}.
   * 
   * @param modemLine
   *          The line of data sent by the {@link Modem}.
   * @return An {@link Event.OnRing} if the modem is sending an ring
   *         notification, an {@link Event.OnNumber} if the modem is sending an
   *         caller ID notification or null otherwise.
   */
  public Event create(String modemLine);
}
