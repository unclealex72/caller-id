/**
 * Copyright 2012 Alex Jones
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
 * @author alex
 *
 */

package uk.co.unclealex.callerid.squeezebox;

import java.io.IOException;

import javax.inject.Inject;

import com.google.api.client.util.escape.Escaper;
import com.google.api.client.util.escape.PercentEscaper;

/**
 * An interface for connecting with a Logitech media server using its network
 * interface.
 * 
 * @author alex
 * 
 */
public class SqueezeboxCliImpl implements SqueezeboxCli {

  /**
   * The {@link Escaper} to use to escape lines of text sent to the Squeezebox's
   * display command.
   */
  private final Escaper percentEscaper = new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, false);

  /**
   * The {@link Squeezebox} used for raw communication.
   */
  private final Squeezebox squeezebox;
  
  
  /**
   * @param squeezebox
   */
  @Inject
  public SqueezeboxCliImpl(Squeezebox squeezebox) {
    super();
    this.squeezebox = squeezebox;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws IOException
   */
  @Override
  public int countPlayers() throws IOException {
    String playerCount = execute("player count ?");
    return Integer.parseInt(playerCount);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void display(int playerNumber, String topLine, String bottomLine, int secondsToDisplay) throws IOException {
    String playerId = execute(String.format("player id %d ?", playerNumber));
    Escaper percentEscaper = getPercentEscaper();
    String command =
        String.format(
            "%s display %s %s %d",
            playerId,
            percentEscaper.escape(topLine),
            percentEscaper.escape(bottomLine),
            secondsToDisplay);
    execute(command);
  }

  /**
   * Execute a command on the squeezebox server.
   * 
   * @param command
   *          The command to execute.
   * @return The response from the squeezebox, minus the echoed command
   *         response.
   * @throws IOException
   */
  public String execute(String command) throws IOException {
    return getSqueezebox().execute(command);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws IOException {
    getSqueezebox().close();
  }
  
  public Escaper getPercentEscaper() {
    return percentEscaper;
  }

  public Squeezebox getSqueezebox() {
    return squeezebox;
  }

}
