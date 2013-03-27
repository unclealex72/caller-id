/**
 * Copyright 2013 Alex Jones
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
 * @author unclealex72
 *
 */

package uk.co.unclealex.callerid.remote.view;

import org.junit.Test
import static org.junit.Assert.*
import org.codehaus.jackson.map.ObjectMapper

/**
 * Test that a call can be serialised into and deserialised from JSON.
 * 
 * @author alex
 * 
 */
class CallJsonTest {
  
  val call = new Call("1972-09-05T09:12Z", "44", "1256", "888999", "United Kingdom", "Basingstoke", "Brian May", "Queenland")
  val mapper = new ObjectMapper
  
  val serialisedCall = #[
      "callReceivedTime" -> "1972-09-05T09:12Z",
      "internationalPrefix" -> "44",
      "stdCode" -> "1256",
      "number" -> "888999",
      "country" -> "United Kingdom",
      "city" -> "Basingstoke",
      "contactName" -> "Brian May",
      "contactAddress" -> "Queenland"
    ].join("{", ",", "}", ['''"«key»":"«value»"'''])
    
  @Test
  def void testSerialisation() {
    val actualOutput = mapper.writeValueAsString(call);
    assertEquals("The call was serialised incorrectly", serialisedCall, actualOutput)
  }

  @Test
  def void testDeserialisation() {
    val deserialisedCall = mapper.readValue(serialisedCall, typeof(Call));
    assertEquals("The call was deserialised incorrectly", call, deserialisedCall)
  }
}
