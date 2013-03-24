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

package uk.co.unclealex.callerid.device;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface for communicating with a device that listens for and responds to
 * lines of text.
 * 
 * @author alex
 * 
 */
interface Device extends Closeable {

  /**
   * Read a line from the device. This method blocks until the modem has data to
   * send.
   * 
   * @return The line read from the streamer or null if the streamer has
   *         disconnected.
   * @throws IOException
   */
  def String readLine() throws IOException;

  /**
   * Write a line to the device. The command line must not be terminated with a
   * newline character.
   * 
   * @param command
   *          The command to send.
   * @throws IOException
   */
  def void writeLine(String command) throws IOException;

}
