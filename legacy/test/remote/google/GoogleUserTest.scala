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

package legacy.remote.google

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * @author alex
 *
 */
class GoogleUserTest extends FunSuite with ShouldMatchers {

  test("Serialise a user") {
    GoogleUser("freddie.mercury@gmail.com", "Freddie Mercury").serialise should
      equal("freddie.mercury@gmail.com:Freddie Mercury")
  }

  test("Pattern match a valid user") {
    GoogleUser parse "freddie.mercury@gmail.com:Freddie Mercury" should
      equal(Some(GoogleUser("freddie.mercury@gmail.com", "Freddie Mercury")))
  }

  test("Pattern match an empty string") {
    GoogleUser parse "" should equal(None)
  }

  test("Pattern match a string with no separators") {
    GoogleUser parse "freddie.mercury@gmail.com" should equal(None)
  }

  test("Pattern match a string with too many separators") {
    GoogleUser parse "freddie.mercury@gmail.com:Freddie:Mercury" should equal(None)
  }

  test("Pattern match a string with no email") {
    GoogleUser parse ":Freddie Mercury" should equal(None)
  }

  test("Pattern match a string with no name") {
    GoogleUser parse "freddie.mercury@gmail.com:" should equal(None)
  }

  test("Pattern match a string with neither email nor name") {
    GoogleUser parse ":" should equal(None)
  }
}
