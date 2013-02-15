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

package uk.co.unclealex.callerid.device

import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import javax.annotation.PreDestroy
import org.eclipse.xtend.lib.Property
import java.io.IOException
import javax.inject.Inject

/**
 * A device that is connected via a network port.
 * 
 * @author alex
 * 
 */
public class NetworkDevice extends AbstractStreamDevice {

  @Property val Socket socket;
  
  protected new(Socket socket, Charset charset) throws IOException {
    super(socket.inputStream, socket.outputStream, charset);
    _socket = socket;
  }

  @Inject
  public new(InetAddress inetAddress, int port, Charset charset) throws IOException {
      this(new Socket(inetAddress, port), charset);
  }
  
  @Inject
  public new(int port, Charset charset) throws IOException {
    this(InetAddress::loopbackAddress, port, charset);
  }
  
  /**
   * {@inheritDoc}
   */
  @PreDestroy
  override close() {
    try {
      super.close();
    }
    finally {
      socket.close();
    }
  }
}
