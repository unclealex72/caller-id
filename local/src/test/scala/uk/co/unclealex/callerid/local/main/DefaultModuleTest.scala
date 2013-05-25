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

package uk.co.unclealex.callerid.local.main

import scala.collection.GenTraversableOnce
import org.scalamock.specs2.MockFactory
import org.specs2.data.Sized
import org.specs2.mutable.Specification
import org.specs2.text.LinesContent
import java.net.Socket
import com.google.inject.Guice
import java.net.ServerSocket
import resource._
import java.io.ByteArrayInputStream
import uk.co.unclealex.callerid.local.device.IoDevice
import java.io.ByteArrayOutputStream
/**
 * @author alex
 *
 */
class DefaultModuleTest extends Specification with MockFactory {

  "The Guice default module" should {
    "be able to to be created" in {
      val ioDevice = new IoDevice() {
        def readLine = Some("")
        def writeLine(line: String) = {}
        def close = {}
      }
      val ioDeviceFactory = mockFunction[String, String, Int, IoDevice]
      List(("modem", "localhost", 999), ("squeezebox", "nonlocalhost", 9990)) map {
        case (name, host, port) =>
          ioDeviceFactory expects (name, host, port) returns ioDevice
      }
      Guice.createInjector(DefaultModule(ioDeviceFactory))
      success
    }
  }
}