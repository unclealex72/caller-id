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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.callerid.modem.listener.CallListener;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * The default implementation of {@link EventNotifier}.
 * 
 * @author alex
 * 
 */
public class EventNotifierImpl implements EventNotifier {

  /** The Constant log. */
  private static final Logger log = LoggerFactory.getLogger(EventNotifierImpl.class);

  /**
   * A {@link Predicate} that wraps an {@link Event}, swallowing, but logging,
   * any thrown exceptions.
   * 
   * @author alex
   * 
   */
  static class ContinuePredicate implements Predicate<CallListener> {

    /**
     * The {@link Event} to wrap.
     */
    private final Event event;

    /**
     * Instantiates a new continue predicate.
     * 
     * @param event
     *          the event
     */
    public ContinuePredicate(Event event) {
      super();
      this.event = event;
    }

    /**
     * Execute an {@link Event} against a {@link CallListener}.
     * 
     * @param callListener
     *          The {@link CallListener} to execute.
     * @return True if execution should continue, false otherwise.
     */
    @Override
    public boolean apply(CallListener callListener) {
      try {
        return getEvent().onEvent(callListener);
      }
      catch (Exception e) {
        log.error("Call listener " + callListener.getClass() + " threw an error.", e);
        return true;
      }
    }

    /**
     * Gets the {@link Event} to wrap.
     * 
     * @return the {@link Event} to wrap
     */
    public Event getEvent() {
      return event;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void notify(Event event, Iterable<CallListener> callListeners) {
    Predicate<CallListener> predicate = new ContinuePredicate(event);
    Iterables.all(callListeners, predicate);
  }

}
