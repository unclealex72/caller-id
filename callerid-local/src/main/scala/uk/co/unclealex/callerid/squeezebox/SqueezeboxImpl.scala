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

package uk.co.unclealex.callerid.squeezebox

import com.google.api.client.util.escape.Escaper
import com.google.api.client.util.escape.PercentEscaper

import uk.co.unclealex.callerid.device.Device
import uk.co.unclealex.process.packages.PackagesRequired

/**
 * The default implementation of Squeezbox that talks to squeezeboxes
 * via the Network CLI.
 *
 * @author alex
 *
 */
@PackagesRequired(Array("logitechmediaserver"))
class SqueezeboxImpl(squeezeboxDevice: Device) extends Squeezebox {

  val percentEscaper: Escaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false);

  /**
   * {@inheritDoc}
   */
  override def displayText(topLine: String, bottomLine: String) {
    0 until countPlayers foreach (displayText(_, topLine, bottomLine))
  }

  /**
   * Display two lines of text on a player.
   * @param player The number of the player.
   * @param topLine The top line of text to display.
   * @param bottomLine the bottom line of text to display.
   */
  def displayText(player: Int, topLine: String, bottomLine: String) {
    var playerId = execute("player id %d ?", player)
    execute(
      "%s display %s %s %d",
      playerId,
      percentEscaper.escape(topLine),
      percentEscaper.escape(bottomLine),
      30)
  }

  def countPlayers: Int =
    Integer.parseInt(execute("player count ?").get)

  def execute(command: String, args: Any*): Option[String] = {
    var fullCommand = String.format(command, args);
    squeezeboxDevice.writeLine(fullCommand);
    squeezeboxDevice.readLine.map(
      response => if (fullCommand.endsWith("?")) response.substring(fullCommand.length - 1) else response)
  }
}
