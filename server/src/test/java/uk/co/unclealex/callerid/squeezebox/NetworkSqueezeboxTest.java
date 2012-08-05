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

package uk.co.unclealex.callerid.squeezebox;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author alex
 *
 */
public class NetworkSqueezeboxTest {

  @Test
  public void testQueryCommand() throws IOException {
    runTest("player count ?", "player count 2", "2");
  }

  @Test
  public void testNonQueryCommand() throws IOException {
    runTest("xx display", "ok", "ok");
  }

  @Test
  public void testNullCommand() throws IOException {
    runTest("xx display", null, null);
  }

  protected void runTest(String command, String response, String expectedResult) throws IOException {
    NetworkSqueezebox networkSqueezebox = mock(TestNetworkSqueezebox.class);
    when(networkSqueezebox.readLine()).thenReturn(response);
    when(networkSqueezebox.execute(command)).thenCallRealMethod();
    String actualResult = networkSqueezebox.execute(command);
    verify(networkSqueezebox).writeLine(command);
    verify(networkSqueezebox).readLine();
    Assert.assertEquals("The wrong result was returned.", expectedResult, actualResult);
  }
  
  class TestNetworkSqueezebox extends NetworkSqueezebox {
    public TestNetworkSqueezebox() {
      super(0);
    }
  }
}
