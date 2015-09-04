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
 * http://www.apache.org/licenses/LICENSE-2.0
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

package squeezebox

import java.io.IOException

import com.google.api.client.util.escape.{Escaper, PercentEscaper}
import com.typesafe.scalalogging.StrictLogging
import device.IoDevice
import util.Provider

/**
 * The default implementation of Squeezebox that talks to squeezeboxes
 * via the Network CLI.
 *
 * @author alex
 *
 */
//@PackagesRequired(Array("logitechmediaserver"))
class SqueezeboxImpl(ioDeviceProvider: Provider[IoDevice]) extends Squeezebox with StrictLogging {

  val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false)

  /**
   * {@inheritDoc}
   */
  override def displayText(topLine: String, bottomLine: String) {
    ioDeviceProvider.withProvided { implicit ioDevice =>
      try {
        0 until countPlayers foreach { player => displayText(topLine, bottomLine, player) }
      } finally {
        ioDevice.writeLine("exit")
      }
    }
  }

  /**
   * Display two lines of text on a player.
   * @param topLine The top line of text to display.
   * @param bottomLine the bottom line of text to display.
   */
  def displayText(topLine: String, bottomLine: String, player: Int)(implicit ioDevice: IoDevice) {
    execute(s"player id $player ?") match {
      case Some(id) => execute(
        s"$id display ${percentEscaper.escape(topLine)} ${percentEscaper.escape(bottomLine)} 30")
      case None => throw new IOException(s"Querying the ID of squeezebox player $player failed.")
    }
  }

  def execute(command: String)(implicit ioDevice: IoDevice): Option[String] = {
    logger info s"Writing command '$command' to the squeezebox server."
    ioDevice writeLine command
    ioDevice.readLine map { response =>
      logger info s"Got response '$response' from the squeezebox server."
      if (command.endsWith("?")) response.substring(command.length - 1) else response
    }
  }

  def countPlayers(implicit ioDevice: IoDevice): Int = {
    val numPattern = "([0-9]+)".r
    execute("player count ?") match {
      case Some(numPattern(cnt)) => Integer.parseInt(cnt)
      case Some(str) => throw new IOException(s"Could not parse the number of squeezebox players: $str")
      case None => throw new IOException("No response was returned when counting the number of squeezebox players.")
    }
  }
}