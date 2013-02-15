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

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import org.eclipse.xtend.lib.Property
import java.nio.charset.Charset

public abstract class AbstractStreamDevice implements Device {

  /**
   * The {@link BufferedReader} used to wrap the supplied {@link InputStream}.
   */
  @Property val BufferedReader reader;

  /**
   * The {@link PrintWriter} used to wrap the supplied {@link OutputStream}.
   */
  @Property val PrintWriter writer;

  /**
   * Initialise this device.
   * 
   * @param in
   *          The device's {@link InputStream}.
   * @param out
   *          The device's {@link OutputStream}.
   * @param charset
   *          The {@link Charset} used by the device.
   * @throws IOException
   *           Thrown if there are any I/O issues.
   */
  public new(InputStream in, OutputStream out, Charset charset) throws IOException {
    _reader = new BufferedReader(new InputStreamReader(in, charset));
    _writer = new PrintWriter(new OutputStreamWriter(out, charset));
      
  }

  /**
   * {@inheritDoc}
   */
  override String readLine() throws IOException {
    return reader.readLine();
  }

  /**
   * {@inheritDoc}
   */
  override writeLine(String command) throws IOException {
    writer.println(command);
    writer.flush();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws IOException
   */
  override close() throws IOException {
    try {
      reader.close();
    }
    finally {
      writer.close();
    }
  }
}
