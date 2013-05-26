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

package uk.co.unclealex.callerid.local.modem

import org.scalamock.specs2.MockFactory
import org.specs2.mutable.Specification
import javax.inject.Provider
import uk.co.unclealex.callerid.local.call.CallController
import uk.co.unclealex.callerid.local.device.IoDevice
import java.io.IOException

/**
 * @author alex
 *
 */
class ModemListenerTest extends Specification with MockFactory {

  "The modem listener" should {
    "initialise the modem" in {
      val modemDevice = mock[IoDevice]
      List("ATZ", "AT+FCLASS=1.0", "AT+VCID=1") foreach {
        line => (modemDevice.writeLine _) expects line
      }
      val modemListener = new ModemListenerImpl(new Provider[IoDevice]() { def get = modemDevice }, mock[CallController])
      modemListener initialiseModem modemDevice
    }
    "read lines until the modem device is disconnected" in {
      val modemDevice = mock[IoDevice]
      val callController = mock[CallController]
      List("OK", "NMBR = P", "NMBR = 999888", "NMBR = 444555") foreach {
        line =>
          (modemDevice.readLine _) expects () returning Some(line)
      }
      inSequence {
        (callController.onCall _) expects "999888" throws new IOException("Oh boy!")
        (callController.onCall _) expects "444555"
        (modemDevice.readLine _) expects () returning None
      }
      val modemListener = new ModemListenerImpl(new Provider[IoDevice]() { def get = modemDevice }, callController)
      modemListener listenForCalls modemDevice
    }
  }
}