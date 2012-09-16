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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.callerid.modem.Modem;
import uk.co.unclealex.callerid.modem.listener.CallListener;

/**
 * The modem controller is the main class that listens to the modem and then
 * sends out {@link CallListener#onRing()} and.
 * 
 * {@link CallListener#onNumber(String)} events.
 * 
 * @author alex
 */
public class ModemController implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(ModemController.class);
  
  /**
   * The {@link Modem} that will be supplying caller ID information.
   */
  private final Modem modem;

  /**
   * A list of {@link CallListener}s that should be notified of rings and
   * numbers.
   */
  private final Iterable<CallListener> callListeners;

  /**
   * The {@link EventFactory} used to convert modem data into {@link Event}s.
   */
  private final EventFactory eventFactory;

  /**
   * The {@link EventNotifier} used to notify {@link CallListener}s of {@link Event}s.
   */
  private final EventNotifier eventNotifier;

  /**
   * @param modem
   * @param callListeners
   * @param eventFactory
   * @param eventNotifier
   */
  public ModemController(
      Modem modem,
      Iterable<CallListener> callListeners,
      EventFactory eventFactory,
      EventNotifier eventNotifier) {
    super();
    this.modem = modem;
    this.callListeners = callListeners;
    this.eventFactory = eventFactory;
    this.eventNotifier = eventNotifier;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    try {
      initialiseModem();
      listenToModem();
    }
    catch (IOException e) {
      log.error("An error occured whilst communicating with the modem.", e);
    }
  }

  /**
   * Tell the modem to send caller ID information.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void initialiseModem() throws IOException {
    Modem modem = getModem();
    for (String command : new String[] { "ATZ", "AT+FCLASS=1.0", "AT+VCID=1" }) {
      modem.writeLine(command);
    }
  }

  /**
   * Listen to the modem.
   * @throws IOException 
   */
  protected void listenToModem() throws IOException {
    Modem modem = getModem();
    String line;
    while ((line = modem.readLine()) != null) {
      notifyListeners(line.trim());
    }
  }

  /**
   * @param onNumberEvent
   */
  protected void notifyListeners(String line) {
    final Event event = getEventFactory().create(line);
    if (event != null) {
      getEventNotifier().notify(event, getCallListeners());
    }
  }

  /**
   * Gets the {@link Modem} that will be supplying caller ID information.
   * 
   * @return the {@link Modem} that will be supplying caller ID information
   */
  public Modem getModem() {
    return modem;
  }

  /**
   * Gets the a list of {@link CallListener}s that should be notified of rings
   * and numbers.
   * 
   * @return the a list of {@link CallListener}s that should be notified of
   *         rings and numbers
   */
  public Iterable<CallListener> getCallListeners() {
    return callListeners;
  }

  public EventFactory getEventFactory() {
    return eventFactory;
  }

  public EventNotifier getEventNotifier() {
    return eventNotifier;
  }

}
