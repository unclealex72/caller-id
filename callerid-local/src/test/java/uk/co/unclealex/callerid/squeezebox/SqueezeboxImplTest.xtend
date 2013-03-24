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

package uk.co.unclealex.callerid.squeezebox

import org.junit.Test
import uk.co.unclealex.callerid.device.Device

import static org.junit.Assert.*
import static org.mockito.Mockito.*
import static org.hamcrest.Matchers.*;
import java.util.Map
import java.util.List

/**
 * @author alex
 *
 */
class SqueezeboxImplTest {

  @Test
  def testQueryCommand() {
    runCommandTest("player count ?", "player count 2", "2");
  }

  @Test
  def testNonQueryCommand() {
    runCommandTest("xx display", "ok", "ok");
  }

  @Test
  def testNullCommand() {
    runCommandTest("xx display", null, null);
  }

  @Test
  def testDisplayToTwoSqueezeboxes() {
    val responses = #{
        "player count ?" -> "player count 2",
        "player id 0 ?" -> "player id 0 00:11",
        "player id 1 ?" -> "player id 1 00:22"
    };
    val device = new MapDevice(responses);
    val squeezebox = new SqueezeboxImpl(device);
    squeezebox.displayText("Top Line", "Bottom Line!");
    assertThat("The wrong commands were sent to the squeezebox", device.commands, 
        contains(
        "player count ?", 
        "player id 0 ?", 
        "00:11 display Top%20Line Bottom%20Line%21 30", 
        "player id 1 ?", 
        "00:22 display Top%20Line Bottom%20Line%21 30")
    )
  }
  
  def protected runCommandTest(String command, String response, String expectedResult) {
    val Device device = mock(typeof(Device));
    when(device.readLine).thenReturn(response);
    val SqueezeboxImpl squeezebox = new SqueezeboxImpl(device);
    val String actualResult = squeezebox.execute(command);
    verify(device).writeLine(command);
    verify(device).readLine();
    assertEquals("The wrong result was returned.", expectedResult, actualResult);
  }

}

class MapDevice implements Device {

    @Property val Map<String, String> responses;
    @Property var String nextResponse;
    @Property val List<String> commands = newArrayList();
    
    public new(Map<String, String> responses) {
      _responses = responses;
    }
    
    override writeLine(String command) {
        nextResponse = responses.get(command);
        commands += command;
    }
    
    override readLine() {
        val response = nextResponse;
        nextResponse = null;
        return response;
    }
    
    override close() {
        // Do nothing.
    }
}
