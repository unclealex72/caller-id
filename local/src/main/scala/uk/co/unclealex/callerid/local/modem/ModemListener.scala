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

import scala.collection.immutable.Stream
import uk.co.unclealex.callerid.local.call.CallController
import uk.co.unclealex.callerid.local.device.Device
import javax.inject.Inject
import com.typesafe.scalalogging.slf4j.Logging

//@PackagesRequired(Array("ser2net"))
class ModemListener @Inject() (modemDevice: Device, callController: CallController) extends Runnable with Logging {

  /**
   * Initialise the modem and then listen for calls.
   */
  override def run() {
    initialiseModem
    listenForCalls
  }

  /**
   * Send any required initilisation command strings to the modem.
   */
  def initialiseModem: Unit = {
    List("ATZ", "AT+FCLASS=1.0", "AT+VCID=1") foreach { line =>
      logger info s"Writing line '$line' to the modem."
      modemDevice writeLine line
    }
  }

  /**
   * Listen for any calls and then notify the {@link #callController}.
   */
  def listenForCalls: Unit = {
    Stream continually (modemDevice readLine) takeWhile (_ isDefined) foreach {
      case Some(line) => {
        logger info s"Received '$line' from the modem"
        callController onCall line
      }
      case None => logger info "Disconnecting from the modem."
    }
  }

}
