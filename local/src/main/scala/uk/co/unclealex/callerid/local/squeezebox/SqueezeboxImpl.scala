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

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.nio.charset.StandardCharsets

import com.google.api.client.util.escape.Escaper
import com.google.api.client.util.escape.PercentEscaper

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
class SqueezeboxImpl @Inject() (@Named("squeezebox") squeezeboxProvider: Provider[IoDevice]) extends Squeezebox {

  val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false);

  /**
   * {@inheritDoc}
   */
  override def displayText(topLine: String, bottomLine: String) {
    def textDisplayer(implicit ioDevice: IoDevice) =
      0 until countPlayers foreach (displayText(_, topLine, bottomLine))
    val ioDevice = squeezeboxProvider.get
    try {
      textDisplayer(ioDevice)
    } finally {
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
    val numPattern = "[0-9]+".r
    execute("player count ?") match {
      case Some(numPattern(cnt)) => Integer.parseInt(cnt)
      case _ => throw new IOException("Could not count the number of squeezebox players")
    }
  }

  def execute(command: String, args: Any*)(implicit ioDevice: IoDevice): Option[String] = {
    ioDevice.writeLine(command)
    ioDevice.readLine map { response =>
      if (command.endsWith("?")) response.substring(command.length - 1) else response
    }
  }
}
