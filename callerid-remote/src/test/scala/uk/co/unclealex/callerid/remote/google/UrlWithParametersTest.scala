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
package uk.co.unclealex.callerid.remote.google;

import java.net.URL
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import uk.co.unclealex.callerid.remote.google.UrlWithParameters._

/**
 * @author alex
 *
 */
class UrlWithParametersTest extends FunSuite with ShouldMatchers {

  implicit class UrlWithParametersTestImplicits(url: String) {

    def parseAndExpect(expectedUrl: String, expectedParameters: Pair[String, String]*) = {
      val expectedUrlWithParameters = UrlWithParameters(expectedUrl, expectedParameters.toMap)
      val actualUrlWithParameters: UrlWithParameters = UrlWithParameters(url)
      actualUrlWithParameters should equal(expectedUrlWithParameters)
    }

    def isExpectedFrom(actualUrl: String, parameters: Pair[String, String]*) = {
      val actualUrlWithParameters = UrlWithParameters(actualUrl, parameters.toMap)
      actualUrlWithParameters should equal(new URL(url))
    }

  }

  test("parse with no parameters") {
    "http://www.dur.ac.uk" parseAndExpect ("http://www.dur.ac.uk")
  }

  test("parse no parameters with trailing question mark") {
    "http://www.dur.ac.uk?" parseAndExpect ("http://www.dur.ac.uk")
  }

  test("parse one parameter") {
    "http://www.dur.ac.uk?course=computing" parseAndExpect ("http://www.dur.ac.uk", "course" -> "computing")
  }

  test("parse two parameters") {
    "http://www.dur.ac.uk?course=computing+science&year=3" parseAndExpect ("http://www.dur.ac.uk", "course" -> "computing science",
      "year" -> "3")
  }

  test("serialise no parameters") {
    "http://www.dur.ac.uk" isExpectedFrom ("http://www.dur.ac.uk")
  }

  test("serialise one parameter") {
    "http://www.dur.ac.uk?course=computing" isExpectedFrom ("http://www.dur.ac.uk", "course" -> "computing")
  }

  test("serialise two parameters") {
    "http://www.dur.ac.uk?course=computing+science&year=3" isExpectedFrom ("http://www.dur.ac.uk", "course" -> "computing science",
      "year" -> "3")
  }
}
