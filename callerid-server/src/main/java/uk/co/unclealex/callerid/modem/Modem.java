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

package uk.co.unclealex.callerid.modem;

import java.io.IOException;

import javax.annotation.PostConstruct;

/**
 * An interface for communicating with a Hayes modem. Consumers of this class
 * have the ability to read a line from the modem and also to write a line to
 * the modem.
 * 
 * @author alex
 * 
 */
public interface Modem extends AutoCloseable {

  /**
   * Initialise the modem ready for use. Implementations should make sure this
   * is called before the modem is used, most likely by annotating this method
   * with {@link PostConstruct}.
   * @throws IOException 
   */
  public void initialise() throws IOException;

  /**
   * Read a line from the modem. This method blocks until the modem has data to
   * send.
   * 
   * @return The line read from the modem or null if the modem has disconnected.
   * @throws IOException
   */
  public String readLine() throws IOException;

  /**
   * Write a line to the modem. The command line must not be terminated with a
   * newline character.
   * 
   * @param command
   *          The command to send.
   * @throws IOException
   */
  public void writeLine(String command) throws IOException;

}
