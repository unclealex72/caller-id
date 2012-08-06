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

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.callerid.calls.ReceivedCallFactory;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.dates.DateFactory;
import uk.co.unclealex.callerid.modem.listener.CallListener;

/**
 * @author alex
 *
 */
public class EventFactoryImplTest {

  @Test
  public void testRing() throws Exception {
    DateFactory dateFactory = mock(DateFactory.class);
    ReceivedCallFactory receivedCallFactory = mock(ReceivedCallFactory.class);
    TestCallListener callListener = new TestCallListener() {
      public boolean onRing() throws Exception {
        return false;
      }
    };
    runTest("RING", callListener, receivedCallFactory, dateFactory);
    verifyNoMoreInteractions(dateFactory, receivedCallFactory);
  }

  @Test
  public void testWithheld() throws Exception {
    Date now = new Date();
    DateFactory dateFactory = mock(DateFactory.class);
    when(dateFactory.now()).thenReturn(now);
    ReceivedCallFactory receivedCallFactory = mock(ReceivedCallFactory.class);
    TestCallListener callListener = new TestCallListener() {
      @Override
      public boolean onNumber(ReceivedCall receivedCall) throws Exception {
        return false;
      }
    };
    runTest("NMBR = P", callListener, receivedCallFactory, dateFactory);
    verify(receivedCallFactory).create(now, null);
  }

  @Test
  public void testNumber() throws Exception {
    Date now = new Date();
    DateFactory dateFactory = mock(DateFactory.class);
    when(dateFactory.now()).thenReturn(now);
    ReceivedCallFactory receivedCallFactory = mock(ReceivedCallFactory.class);
    TestCallListener callListener = new TestCallListener() {
      @Override
      public boolean onNumber(ReceivedCall receivedCall) throws Exception {
        return false;
      }
    };
    runTest("NMBR = 01256555444", callListener, receivedCallFactory, dateFactory);
    verify(receivedCallFactory).create(now, "01256555444");
  }

  @Test
  public void testPlusNumber() throws Exception {
    Date now = new Date();
    DateFactory dateFactory = mock(DateFactory.class);
    when(dateFactory.now()).thenReturn(now);
    ReceivedCallFactory receivedCallFactory = mock(ReceivedCallFactory.class);
    TestCallListener callListener = new TestCallListener() {
      @Override
      public boolean onNumber(ReceivedCall receivedCall) throws Exception {
        return false;
      }
    };
    runTest("NMBR = +441256555444", callListener, receivedCallFactory, dateFactory);
    verify(receivedCallFactory).create(now, "+441256555444");
  }

  @Test
  public void testMalformedNumber() throws Exception {
    DateFactory dateFactory = mock(DateFactory.class);
    ReceivedCallFactory receivedCallFactory = mock(ReceivedCallFactory.class);
    runTest("NMBR = P01256555444", null, receivedCallFactory, dateFactory);
    verifyNoMoreInteractions(dateFactory, receivedCallFactory);
  }

  protected void runTest(String line, TestCallListener callListener, ReceivedCallFactory receivedCallFactory, DateFactory dateFactory) throws Exception {
    EventFactory eventFactory = new EventFactoryImpl(receivedCallFactory, dateFactory);
    Event event = eventFactory.create(line);
    if (callListener == null) {
      Assert.assertNull("The event should have been null.", event);
    }
    else {
      Assert.assertNotNull("The event should not have been null.", event);
      event.onEvent(callListener);
    }
  }
  
  class TestCallListener implements CallListener {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onRing() throws Exception {
      Assert.fail("The onRing event was called when it was not expected to be.");
      return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNumber(ReceivedCall receivedCall) throws Exception {
      Assert.fail("The onNumber event was called when it was not expected to be.");
      return false;
    }
  }
}
