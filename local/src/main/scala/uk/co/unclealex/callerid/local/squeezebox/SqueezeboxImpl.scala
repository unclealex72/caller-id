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

package uk.co.unclealex.callerid.local.squeezebox

import java.io.IOException

import com.google.api.client.util.escape.Escaper
import com.google.api.client.util.escape.PercentEscaper
import com.typesafe.scalalogging.slf4j.Logging

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import uk.co.unclealex.callerid.local.device.IoDevice

/**
 * The default implementation of Squeezbox that talks to squeezeboxes
 * via the Network CLI.
 *
 * @author alex
 *
 */
//@PackagesRequired(Array("logitechmediaserver"))
class SqueezeboxImpl @Inject() (@Named("squeezebox") squeezeboxProvider: Provider[IoDevice]) extends Squeezebox with Logging {

  val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false);

  /**
   * {@inheritDoc}
   */
  override def displayText(topLine: String, bottomLine: String) {
    implicit val ioDevice = squeezeboxProvider.get
    try {
      0 until countPlayers foreach (displayText(_, topLine, bottomLine))
    } finally {
      ioDevice writeLine "exit"
      ioDevice close
    }
  }

  /**
   * Display two lines of text on a player.
   * @param player The number of the player.
   * @param topLine The top line of text to display.
   * @param bottomLine the bottom line of text to display.
   */
  def displayText(player: Int, topLine: String, bottomLine: String)(implicit ioDevice: IoDevice) {
    execute(s"player id ${player} ?") match {
      case Some(id) => execute(
        s"${id} display ${percentEscaper.escape(topLine)} ${percentEscaper.escape(bottomLine)} 30")
      case None => throw new IOException(s"Querying the ID of squeezebox player ${player} failed.")
    }
  }

  def countPlayers(implicit ioDevice: IoDevice): Int = {
    val numPattern = "([0-9]+)".r
    execute("player count ?") match {
      case Some(numPattern(cnt)) => Integer.parseInt(cnt)
      case Some(str) => throw new IOException(s"Could not parse the number of squeezbox players: $str")
      case None => throw new IOException("No response was returned when counting the number of squeezebox players.")
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
}