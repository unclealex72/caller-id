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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import uk.co.unclealex.callerid.modem.listener.CallListener;

/**
 * @author alex
 *
 */
public class EventNotifierImplTest {

  EventNotifier eventNotifier = new EventNotifierImpl();
  
  @Test
  public void testAllCalled() throws Exception {
    CallListener c1 = mock(CallListener.class);
    CallListener c2 = mock(CallListener.class);
    when(c1.onRing()).thenReturn(true);
    when(c2.onRing()).thenReturn(true);
    eventNotifier.notify(new Event.OnRing(), Arrays.asList(c1, c2));
    verify(c1).onRing();
    verify(c2).onRing();
    verifyNoMoreInteractions(c1, c2);
  }

  @Test
  public void testAllCalledWithException() throws Exception {
    CallListener c1 = mock(CallListener.class);
    CallListener c2 = mock(CallListener.class);
    when(c1.onRing()).thenThrow(new IOException());
    when(c2.onRing()).thenReturn(true);
    eventNotifier.notify(new Event.OnRing(), Arrays.asList(c1, c2));
    verify(c1).onRing();
    verify(c2).onRing();
    verifyNoMoreInteractions(c1, c2);
  }

  @Test
  public void testAllCalledWithRuntimeException() throws Exception {
    CallListener c1 = mock(CallListener.class);
    CallListener c2 = mock(CallListener.class);
    when(c1.onRing()).thenThrow(new IOException());
    when(c2.onRing()).thenReturn(true);
    eventNotifier.notify(new Event.OnRing(), Arrays.asList(c1, c2));
    verify(c1).onRing();
    verify(c2).onRing();
    verifyNoMoreInteractions(c1, c2);
  }

  @Test
  public void testFalseHaltsExecution() throws Exception {
    CallListener c1 = mock(CallListener.class);
    CallListener c2 = mock(CallListener.class);
    when(c1.onRing()).thenReturn(false);
    when(c2.onRing()).thenReturn(true);
    eventNotifier.notify(new Event.OnRing(), Arrays.asList(c1, c2));
    verify(c1).onRing();
    verifyNoMoreInteractions(c1, c2);
  }
}
