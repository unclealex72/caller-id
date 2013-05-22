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
 * @author alex
 *
 */
package uk.co.unclealex.callerid.local.configuration

import org.specs2.mutable.Specification

class ConfigurationFactoryTest extends Specification {

  "A configuration factory" should {
    val configurationFactory = new ConfigurationFactory(getClass().getClassLoader().getResource("test-configuration.json"))
    "be able to read a remote configuration" in {
      configurationFactory.load[RemoteConfiguration] must be equalTo
        RemoteConfiguration("https://www.somewhere.com/api", username = "Brian", password = "Br1an")
    }
    "be able to read a modem configuration" in {
      configurationFactory.load[ModemConfiguration] must be equalTo
        ModemConfiguration("localhost", 999)
    }
  }

}
