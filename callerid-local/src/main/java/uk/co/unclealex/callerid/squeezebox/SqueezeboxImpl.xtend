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
import javax.inject.Inject
import org.eclipse.xtend.lib.Property
import uk.co.unclealex.callerid.device.Device

/**
 * The default implementation of {@link Squeezbox} that talks to squeezeboxes
 * via the Network CLI.
 * 
 * @author alex
 * 
 */
public class SqueezeboxImpl implements Squeezebox {
  
  @Property val Device squeezeboxDevice;
  @Property val Escaper percentEscaper = new PercentEscaper(PercentEscaper::SAFECHARS_URLENCODER, false);
  
  
  @Inject
  public new(@SqueezeboxDevice Device squeezeboxDevice) {
      _squeezeboxDevice = squeezeboxDevice;
  }
  
  /**
   * {@inheritDoc}
   */
  override displayText(String topLine, String bottomLine) {
    (0..countPlayers-1).forEach( [ player | displayText(player, topLine, bottomLine) ]);
  }

    /**
     * Display two lines of text on a player.
     * @param player The number of the player.
     * @param topLine The top line of text to display.
     * @param bottomLine the bottom line of text to display.
     */
  def protected void displayText(int player, String topLine, String bottomLine) {
    var String playerId = execute(String::format("player id %d ?", player));
    execute(String::format(
            "%s display %s %s %d",
            playerId,
            percentEscaper.escape(topLine),
            percentEscaper.escape(bottomLine),
            30));
  }
  
  def protected countPlayers() {
    Integer::parseInt(execute("player count ?"));
  }
  
  def protected String execute(String command) {
      squeezeboxDevice.writeLine(command);
      squeezeboxDevice.readLine;
  }
}
