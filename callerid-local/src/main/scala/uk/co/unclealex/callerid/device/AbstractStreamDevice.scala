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
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset

abstract class AbstractStreamDevice(
  /**
   * The {@link BufferedReader} used to wrap the supplied {@link InputStream}.
   */
  reader: BufferedReader,
  /**
   * The {@link PrintWriter} used to wrap the supplied {@link OutputStream}.
   */
  writer: BufferedWriter) extends Device {

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
  def this(in: InputStream, out: OutputStream, charset: Charset) {
    this(
      new BufferedReader(new InputStreamReader(in, charset)),
      new BufferedWriter(new OutputStreamWriter(out, charset)))
  }

  /**
   * {@inheritDoc}
   */
  override def readLine =
    Option.apply(reader.readLine)

  /**
   * {@inheritDoc}
   */
  override def writeLine(command: String) {
    writer.write(command);
    writer.write('\n')
    writer.flush();
  }

  /**
   * {@inheritDoc}
   *
   * @throws IOException
   */
  override def close() {
    try {
      reader.close();
    } finally {
      writer.close();
    }
  }
}
