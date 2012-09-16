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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import uk.co.unclealex.callerid.calls.ReceivedCallFactory;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.dates.DateFactory;
import uk.co.unclealex.callerid.modem.Modem;

/**
 * The default implementation of {@link EventFactory}.
 * @author alex
 *
 */
public class EventFactoryImpl implements EventFactory {

  /**
   * The command sent by the {@link Modem} when the phone starts ringing.
   */
  private static final String RING = "RING";

  /**
   * The string the modem sends for a withheld number.
   */
  private static final String WITHHELD = "P";
  
  /**
   * The regular expression that matches the output the {@link Modem} produces
   * when it announces a number is calling.
   */
  private static final Pattern NUMBER_PATTERN = Pattern.compile("NMBR = (" + WITHHELD + "|\\+?[0-9]+)");

  /**
   * The {@link ReceivedCallFactory} used to generate {@link ReceivedCall}s.
   */
  private final ReceivedCallFactory receivedCallFactory;
  
  /**
   * The {@link DateFactory} used to get the current time.
   */
  private final DateFactory dateFactory;

  
  /**
   * @param receivedCallFactory
   * @param dateFactory
   */
  @Inject
  public EventFactoryImpl(ReceivedCallFactory receivedCallFactory, DateFactory dateFactory) {
    super();
    this.receivedCallFactory = receivedCallFactory;
    this.dateFactory = dateFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Event create(String line) {
    if (RING.equals(line)) {
      return new Event.OnRing();
    }
    else {
      Matcher matcher = NUMBER_PATTERN.matcher(line);
      if (matcher.matches()) {
        String receivedNumber = matcher.group(1);
        String number = WITHHELD.equals(receivedNumber) ? null : receivedNumber;
        ReceivedCall receivedCall = getReceivedCallFactory().create(getDateFactory().now(), number);
        return new Event.OnNumber(receivedCall);
      }
      return null;
    }
  }

  public ReceivedCallFactory getReceivedCallFactory() {
    return receivedCallFactory;
  }

  public DateFactory getDateFactory() {
    return dateFactory;
  }

}
