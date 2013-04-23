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

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import scala.io.Source
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * Test that calls can be serialised into and deserialised from JSON.
 *
 * @author alex
 *
 */
class CallJsonTest extends FunSuite with ShouldMatchers with GivenWhenThen {

  test("GeographicWithContactAndAddress") {
    "call-geographic-with-contact-and-address.json" deserialisesTo (
      Call("2012-09-05T09:12T+01:00",
        new Number("44", Some("1256"), "362362"),
        new Location("United Kingdom", "GB", Some("Basingstoke")),
        Some(new Contact("Beechdown Health Club", Some("Beechdown Park  Winchester Rd, Basingstoke, RG22 4ES")))))
  }

  test("GeographicWithContactButNoAddress") {
    "call-geographic-with-contact-but-no-address.json" deserialisesTo (
      Call("2012-09-05T09:12T+01:00",
        new Number("44", Some("1483"), "550550"),
        new Location("United Kingdom", "GB", Some("Guildford")),
        Some(new Contact("University of Surrey", None))))
  }

  test("GeographicWithoutContact") {
    "call-geographic-without-contact.json" deserialisesTo (
      Call(
        "2012-11-05T15:10Z",
        new Number("44", Some("1256"), "362362"),
        new Location("United Kingdom", "GB", Some("Basingstoke")),
        None))
  }

  test("NonGeographicWithContactAndAddress") {
    "call-non-geographic-with-contact-and-address.json" deserialisesTo (
      Call(
        "2012-09-05T09:12T+01:00",
        new Number("1", None, "800362362"),
        new Location("United States of America", "US", None),
        Some(new Contact("American Airlines", Some("Los Angeles International Airport, 400 World Way, Los Angeles, CA 90045")))))
  }

  test("NonGeographicWithContactButNoAddress") {
    "call-non-geographic-with-contact-but-no-address.json" deserialisesTo (
      Call(
        "2012-09-05T09:12T+01:00",
        new Number("44", None, "7012550550"),
        new Location("United Kingdom", "GB", None),
        Some(new Contact("University of Surrey", None))))
  }

  test("NonGeographicWithoutContact") {
    "call-non-geographic-without-contact.json" deserialisesTo (
      Call(
        "2012-11-05T15:10Z",
        new Number("33", null, "800162362"),
        new Location("France", "FR", null),
        null))
  }

  implicit class TestCase(resourceName: String) {
    def deserialisesTo(call: Call) {
      val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)
      val resourceContent = Source.fromURL(getClass.getClassLoader.getResource(resourceName)).mkString("\n")
      Given(s"The resource ${resourceName}")
      When(s"Trying to deserialise it")
      val deserialisedJsonCall = objectMapper.readValue(resourceContent, classOf[Call])
      Then("It should deserialise correctly")
      deserialisedJsonCall should equal(call)
      When("Trying to serialise it back")
      val serialisedCall = objectMapper.writer.withDefaultPrettyPrinter.writeValueAsString(call)
      Then("It should serialise back to its original form")
      serialisedCall should equal(resourceContent)
    }

  }
}
