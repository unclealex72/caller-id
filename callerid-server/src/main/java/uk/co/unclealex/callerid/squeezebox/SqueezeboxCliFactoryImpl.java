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

/**
 * The default implementation of {@link SqueezeboxCliFactory}.
 * 
 * @author alex
 */
public class SqueezeboxCliFactoryImpl implements SqueezeboxCliFactory {

  /**
   * The port the squeezebox server is listening on.
   */
  private final int port;
  
  /**
   * Instantiates a new squeezebox cli factory impl.
   * 
   * @param port
   *          the port
   */
  public SqueezeboxCliFactoryImpl(int port) {
    super();
    this.port = port;
  }


  /**
   * {@inheritDoc}
   * @throws IOException 
   */
  @Override
  public SqueezeboxCli create() throws IOException {
    NetworkSqueezebox networkSqueezebox = new NetworkSqueezebox(getPort());
    networkSqueezebox.initialise();
    SqueezeboxCli squeezeboxCli = new SqueezeboxCliImpl(networkSqueezebox);
    return squeezeboxCli;
  }


  /**
   * Gets the port the squeezebox server is listening on.
   * 
   * @return the port the squeezebox server is listening on
   */
  public int getPort() {
    return port;
  }

}
