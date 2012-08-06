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
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.process.packages.PackagesRequired;

import com.google.common.base.Charsets;

/**
 * A modem that is connected via a network bridge (e.g. ser2net).
 * @author alex
 *
 */
@PackagesRequired("ser2net")
public class NetworkModem extends AbstractStreamModem {

  /** The Constant log. */
  private static final Logger log = LoggerFactory.getLogger(NetworkModem.class);
  
  /**
   * The port the modem bridge is listening on.
   */
  private final int port;
  
  /**
   * The {@link Charset} the modem uses.
   */
  private final Charset charset;

  /**
   * The {@link Socket} used to listen to the modem.
   */
  private Socket socket;
  
  /**
   * Instantiates a new network modem.
   * 
   * @param port
   *          the port
   * @param charset
   *          the charset
   */
  @Inject
  public NetworkModem(int port, Charset charset) {
    super();
    this.port = port;
    this.charset = charset;
  }

  /**
   * Create a new ASCII network modem.
   * 
   * @param port
   *          the port
   */
  public NetworkModem(int port) {
    this(port, Charsets.US_ASCII);
  }

  /**
   * Initialise.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @PostConstruct
  public void initialise() throws IOException {
    Charset charset = getCharset();
    int port = getPort();
    log.info("Connecting to the network modem on port " + port + " using character set " + charset.name());
    Socket socket = new Socket(InetAddress.getLoopbackAddress(), port);
    setSocket(socket);
    initialise(socket.getInputStream(), socket.getOutputStream(), charset);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @PreDestroy
  public void close() throws IOException {
    try {
      super.close();
    }
    finally {
      getSocket().close();
    }
  }
  /**
   * Gets the port the modem bridge is listening on.
   * 
   * @return the port the modem bridge is listening on
   */
  public int getPort() {
    return port;
  }

  /**
   * Gets the {@link Charset} the modem uses.
   * 
   * @return the {@link Charset} the modem uses
   */
  public Charset getCharset() {
    return charset;
  }

  /**
   * Gets the {@link Socket} used to listen to the modem.
   * 
   * @return the {@link Socket} used to listen to the modem
   */
  public Socket getSocket() {
    return socket;
  }

  /**
   * Sets the {@link Socket} used to listen to the modem.
   * 
   * @param socket
   *          the new {@link Socket} used to listen to the modem
   */
  public void setSocket(Socket socket) {
    this.socket = socket;
  }
  
  
}
