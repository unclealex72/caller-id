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
package uk.co.unclealex.callerid.local.call

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.ObjectMapper
import org.specs2.mutable.Specification

class RemoteConfigurationTest extends Specification {

  "A remote configuration object" should {
    "be deserialisable from JSON" in {
      var json = """{
              "username" : "Brian",
              "password" : "Br1an",
              "url" : "https://www.somewhere.com/api"
          }"""
      val reader = new ObjectMapper().registerModule(DefaultScalaModule).reader(classOf[RemoteConfiguration])
      val actualConfiguration: RemoteConfiguration = reader.readValue(json)
      val expectedConfiguration = RemoteConfiguration("https://www.somewhere.com/api", username = "Brian", password = "Br1an")
      actualConfiguration must be equalTo(expectedConfiguration)
    }
  }

}
