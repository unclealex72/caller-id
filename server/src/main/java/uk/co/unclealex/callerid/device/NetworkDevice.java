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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A device that is connected via a network port.
 * 
 * @author alex
 * 
 */
public class NetworkDevice extends AbstractStreamDevice {

  /** The Constant log. */
  private static final Logger log = LoggerFactory.getLogger(NetworkDevice.class);

  /**
   * The port the device is listening on.
   */
  private final int port;

  /**
   * The host the device is connected to or null if it can be connected to via
   * the loopback device.
   */
  private final InetAddress host;

  /**
   * The {@link Charset} the device uses.
   */
  private final Charset charset;

  /**
   * The {@link Socket} used to listen to the device.
   */
  private Socket socket;

  /**
   * Instantiates a new network device.
   * 
   * @param port
   *          the port
   * @param host
   *          The host.
   * @param charset
   *          the charset
   * @throws UnknownHostException
   */
  @Inject
  public NetworkDevice(int port, String host, Charset charset) throws UnknownHostException {
    super();
    this.port = port;
    this.host = InetAddress.getByName(host);
    this.charset = charset;
  }

  /**
   * Instantiates a new network device.
   * 
   * @param port
   *          the port
   * @param host
   *          The host.
   * @param charset
   *          the charset
   * @throws UnknownHostException
   */
  @Inject
  public NetworkDevice(int port, Charset charset) {
    super();
    this.port = port;
    this.host = InetAddress.getLoopbackAddress();
    this.charset = charset;
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
    InetAddress host = getHost();
    log.info("Connecting to the network device on host "
        + host
        + " and port "
        + port
        + " using character set "
        + charset.name());
    Socket socket = new Socket(host, port);
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
   * Gets the port the device is listening on.
   * 
   * @return the port the device is listening on
   */
  public int getPort() {
    return port;
  }

  /**
   * Gets the {@link Charset} the device uses.
   * 
   * @return the {@link Charset} the device uses
   */
  public Charset getCharset() {
    return charset;
  }

  /**
   * Gets the {@link Socket} used to listen to the device.
   * 
   * @return the {@link Socket} used to listen to the device
   */
  public Socket getSocket() {
    return socket;
  }

  /**
   * Sets the {@link Socket} used to listen to the device.
   * 
   * @param socket
   *          the new {@link Socket} used to listen to the device
   */
  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public InetAddress getHost() {
    return host;
  }

}
