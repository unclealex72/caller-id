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

import org.specs2.mutable.Specification
import org.scalamock.specs2.MockFactory
import uk.co.unclealex.callerid.local.device.Device
import uk.co.unclealex.callerid.local.call.CallController

/**
 * @author alex
 *
 */
class ModemListenerTest extends Specification with MockFactory {

  "The modem listener" should {
    "initialise the modem" in {
      val modemDevice = mock[Device]
      List("ATZ", "AT+FCLASS=1.0", "AT+VCID=1") foreach {
        line => (modemDevice.writeLine _) expects line
      }
      val modemListener = new ModemListener(modemDevice, mock[CallController])
      modemListener initialiseModem
    }
    "read lines until the modem device is disconnected" in {
      val modemDevice = mock[Device]
      val callController = mock[CallController]
      List("Line One", "Line Two", "Line Three") foreach {
        line =>
          (modemDevice.readLine _) expects () returning Some(line)
          (callController.onCall _) expects line
      }
      (modemDevice.readLine _) expects () returning None
      val modemListener = new ModemListener(modemDevice, callController)
      modemListener listenForCalls
    }
  }
}