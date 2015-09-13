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

package modem

import device.IoDevice
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

/**
 * @author alex
 *
 */
class AtzModemSpec extends Specification with Mockito {

  "The modem" should {
    "send the correct initialisation commands" in {
      val modemDevice = mock[IoDevice]
      val modem = new AtzModem(modemDevice)
      modem.initialise()
      List("ATZ", "AT+FCLASS=1.0", "AT+VCID=1") map {
        line => there was one(modemDevice).writeLine(line)
      }
    }

    "read lines until the modem device is disconnected" in {
      val modemDevice = mock[IoDevice]
      modemDevice.readLines returns Stream("OK", "RING", "NMBR = P", "NMBR = 444555", "HUH")
      val modem = new AtzModem(modemDevice)
      modem.responses.toSeq must be_===(Seq(Ok, Ring, Withheld, Number("444555"), Unknown("HUH")))
    }
  }
}