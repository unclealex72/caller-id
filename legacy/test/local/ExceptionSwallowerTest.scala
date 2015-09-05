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

package legacy.local

import legacy.local.ExceptionSwallower
import org.scalamock.specs2.MockFactory
import org.specs2.mutable.Specification

import ExceptionSwallower.swallow

/**
 * @author alex
 *
 */
class ExceptionSwallowerTest extends Specification with MockFactory {

  "Swallowed code that does not throw an exception" should {
    "return true" in {
      val mockedObject = mock[Runnable]
      (mockedObject.run _) expects ()
      val result = swallow("A message") { mockedObject run }
      result must be equalTo true
    }
  }

  "Swallowed code that does throw an exception" should {
    "return false" in {
      val mockedObject = mock[Runnable]
      (mockedObject.run _) expects () throws new RuntimeException("Oi oi!")
      val result = swallow("A message") { mockedObject run }
      result must be equalTo false
    }
  }
}