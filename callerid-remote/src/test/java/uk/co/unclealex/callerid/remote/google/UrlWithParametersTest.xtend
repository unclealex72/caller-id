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

import static org.junit.Assert.*
import static extension uk.co.unclealex.callerid.remote.google.UrlWithParameters.*
import org.junit.Test
import java.net.URL

/**
 * @author alex
 *
 */
class UrlWithParametersTest {

    @Test
    def void testParseNoParameters() {
        "http://www.dur.ac.uk".parseAndExpect("http://www.dur.ac.uk")
    }

    @Test
    def void testParseNoParametersWithTrailingQuestionMark() {
        "http://www.dur.ac.uk?".parseAndExpect("http://www.dur.ac.uk")
    }

    @Test
    def void testParseOneParameter() {
        "http://www.dur.ac.uk?course=computing".parseAndExpect("http://www.dur.ac.uk", "course" -> "computing")
    }

    @Test
    def void testParseTwoParameters() {
        "http://www.dur.ac.uk?course=computing&year=3".parseAndExpect("http://www.dur.ac.uk", "course" -> "computing",
            "year" -> "3")
    }

    @Test
    def void testSerialiseNoParameters() {
        "http://www.dur.ac.uk".isExpectedFrom("http://www.dur.ac.uk")
    }

    @Test
    def void testSerialiseOneParameter() {
        "http://www.dur.ac.uk?course=computing".isExpectedFrom("http://www.dur.ac.uk", "course" -> "computing")
    }

    @Test
    def void testSerialiseTwoParameters() {
        "http://www.dur.ac.uk?course=computing&year=3".isExpectedFrom("http://www.dur.ac.uk", "course" -> "computing",
            "year" -> "3")
    }

    def void parseAndExpect(String url, String expectedUrl, Pair<String, String>... expectedParameters) {
        val UrlWithParameters expectedUrlWithParameters = new UrlWithParameters(expectedUrl, expectedParameters);
        assertEquals("The url was parsed incorrectly", expectedUrlWithParameters, new URL(url).parse)
    }
    
    def void isExpectedFrom(String expectedUrl, String url, Pair<String, String>... parameters) {
        val UrlWithParameters actualUrlWithParameters = new UrlWithParameters(url, parameters)
        assertEquals("The url was serialised incorrectly.", new URL(expectedUrl), actualUrlWithParameters.toURL)
    }
}
