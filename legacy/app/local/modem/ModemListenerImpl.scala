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
package legacy.local.modem

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import legacy.local.ExceptionSwallower
import legacy.local.call.CallController
import legacy.local.device.IoDevice

import scala.collection.immutable.Stream
import scala.util.matching.Regex
import com.typesafe.scalalogging.slf4j.Logging
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import ExceptionSwallower._

//@PackagesRequired(Array("ser2net"))
class ModemListenerImpl @Inject() (
  @Named("modem") modemProvider: Provider[IoDevice], callController: CallController) extends Runnable with Logging {

  /**
   * Initialise the modem and then listen for calls.
   */
  override def run() {
    logger info "Initialising the modem."
    val modem = modemProvider.get
    try {
      initialiseModem(modem)
      logger info "Initialisation succeeded."
      listenForCalls(modem)
    } finally {
      modem close
    }
  }

  /**
   * Send any required initilisation command strings to the modem.
   */
  def initialiseModem(ioDevice: IoDevice) = {
    List("ATZ", "AT+FCLASS=1.0", "AT+VCID=1") foreach { line =>
      logger info s"Writing line '$line' to the modem."
      ioDevice writeLine line
    }
  }

  /**
   * Listen for any calls and then notify the {@link #callController}.
   */
  def listenForCalls(ioDevice: IoDevice): Unit = {
    Stream continually (ioDevice readLine) takeWhile (_.isDefined) map (_.get.trim) filterNot (_.isEmpty) foreach {
      line =>
        line match {
          case Ok() => logger info "Received OK from the modem."
          case Ring() => logger info "Received RING from the modem."
          case Witheld() => logger info "Received a witheld number from the modem."
          case Number(number) => {
            logger info s"Received a call from $number via the modem."
            swallow(s"Could not successfully report that there was a call from $number") {
              callController onCall number
            }
          }
          case _ => {
            val bytes = line.getBytes.toList
            val decodedLine =
              if (bytes.forall(by => by >= 32 && by <= 127)) line else bytes.map(b => "%02x".format(b)).mkString(" ")
            logger warn s"Received unknown response '$decodedLine' from the modem."
          }
        }
    }
  }
}

/**
 * An abstract of all the valid modem responses.
 */
sealed abstract class ModemResponse
object ModemResponse {
  def apply[A <: ModemResponse](requiredLine: String, actualLine: String, success: A) =
    if (requiredLine == actualLine) Some(success) else None
}

/**
 * The OK response to a sent command
 */
case object Ok extends ModemResponse {
  def unapply(line: String) = ModemResponse("OK", line, Ok)
}

/**
 * The RING command when the phone rings.
 */
case object Ring extends ModemResponse {
  def unapply(line: String) = ModemResponse("RING", line, Ring)
}

/**
 * The response when a witheld number calls.
 */
case object Witheld extends ModemResponse {
  def unapply(line: String) = ModemResponse("NMBR = P", line, Witheld)
}

/**
 * The response when a non-witheld number calls.
 */
case class Number(
  /**
   * The number sent via the modem.
   */
  number: String) extends ModemResponse
object Number {
  def unapply(line: String): Option[String] = {
    val pattern = new Regex("NMBR = ([0-9]+)")
    pattern findFirstIn line match {
      case Some(pattern(number)) => Some(number)
      case None => None
    }
  }
}