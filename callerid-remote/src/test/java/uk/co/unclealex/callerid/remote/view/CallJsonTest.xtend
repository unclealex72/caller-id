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

import com.google.common.io.CharStreams
import com.google.common.io.InputSupplier
import static org.junit.Assert.*
import org.junit.Test
import java.io.InputStreamReader
import org.codehaus.jackson.map.ObjectMapper

/**
 * Test that calls can be serialised into and deserialised from JSON.
 * 
 * @author alex
 * 
 */
class CallJsonTest {
  
  @Test
  def void testGeographicWithContactAndAddress() {
      test("call-geographic-with-contact-and-address.json", 
          new Call("2012-09-05T09:12T+01:00", 
              new Number("44", "1256", "362362"),
              new Location("United Kingdom", "GB", "Basingstoke"),
              new Contact("Beechdown Health Club", "Beechdown Park  Winchester Rd, Basingstoke, RG22 4ES")
          )
      )
  }

  @Test
  def void testGeographicWithContactButNoAddress() {
      test("call-geographic-with-contact-but-no-address.json", 
          new Call("2012-09-05T09:12T+01:00", 
              new Number("44", "1483", "550550"),
              new Location("United Kingdom", "GB", "Guildford"),
              new Contact("University of Surrey", null)
          )
      )
  }

  @Test
  def void testGeographicWithoutContact() {
      test("call-geographic-without-contact.json",
          new Call(
            "2012-11-05T15:10Z",
            new Number("44", "1256", "362362"),
            new Location("United Kingdom", "GB", "Basingstoke"),
            null
            )
      )
  }

  @Test
  def void testNonGeographicWithContactAndAddress() {
      test("call-non-geographic-with-contact-and-address.json",
          new Call(
              "2012-09-05T09:12T+01:00",
              new Number("1", null, "800362362"),
              new Location("United States of America", "US", null),
              new Contact("American Airlines", "Los Angeles International Airport, 400 World Way, Los Angeles, CA 90045"
              )
          )
      )
  }

  @Test
  def void testNonGeographicWithContactButNoAddress() {
      test("call-non-geographic-with-contact-but-no-address.json", 
          new Call(
              "2012-09-05T09:12T+01:00",
              new Number("44", null, "7012550550"),
              new Location("United Kingdom", "GB", null),
              new Contact("University of Surrey", null)
          )
      )
  }

  @Test
  def void testNonGeographicWithoutContact() {
      test("call-non-geographic-without-contact.json",
          new Call(
            "2012-11-05T15:10Z",
            new Number("33", null, "800162362"),
            new Location("France", "FR", null),
            null
            )
        )
  }
  
  def void test(String callResourceName, Call call) {
      val StringBuilder builder = new StringBuilder
      val InputSupplier<InputStreamReader> supplier = 
      [ | new InputStreamReader(typeof(CallJsonTest).classLoader.getResourceAsStream(callResourceName), "utf-8") ]
      CharStreams::copy(supplier, builder)
      val String json = builder.toString
      val ObjectMapper mapper = new ObjectMapper
      val Call deserialisedJson = mapper.readValue(json, typeof(Call))
      assertEquals("The JSON representation was not deserialised correctly.", call, deserialisedJson)
      val String serialisedCall = mapper.writer.withDefaultPrettyPrinter. writeValueAsString(call)
      assertEquals("The call was not serialised correctly.", json, serialisedCall)
  }
}
