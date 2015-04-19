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

package legacy.module

import org.scalatest.GivenWhenThen
import org.scalatest.FunSuite
import com.google.inject.Injector
import com.google.inject.Guice

/**
 * @author alex
 *
 */
class CallerIdModuleTest extends FunSuite with GivenWhenThen {

  test("Application initialisation") {
    When("starting the application")
    Then("the guice legacy.module should not have any errors.")
    Guice.createInjector(new CallerIdModule)
  }
}