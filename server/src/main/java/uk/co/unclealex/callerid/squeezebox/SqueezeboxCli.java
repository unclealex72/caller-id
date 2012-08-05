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

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface for communicating with a Logitech Squeezebox server via the command line interface.
 * @author alex
 *
 */
public interface SqueezeboxCli extends Closeable {

  /**
   * Return a count of the number of players connected to this server.
   * @return A count of the number of players connected to this server.
   * @throws IOException
   */
  public int countPlayers() throws IOException;
  
  /**
   * Display information on a squeezebox client.
   * @param playerNumber The number of the player that will display the information.
   * @param topLine The string to display on the top line of the screen.
   * @param bottomLine The string to display on the bottom line of the screen.
   * @param secondsToDisplay The number of seconds the message needs to be displayed for.
   */
  public void display(int playerNumber, String topLine, String bottomLine, int secondsToDisplay) throws IOException;
  
}
