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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

/**
 * The base class for modems that use {@link InputStream}s and.
 * 
 * {@link OutputStream}s. Subclasses must call
 * {@link #initialise(InputStream, OutputStream, Charset)} inside a method
 * annotated with {@link PostConstruct}.
 * 
 * @author alex
 */
public abstract class AbstractStreamModem implements Modem {

  /**
   * The {@link BufferedReader} used to wrap the supplied {@link InputStream}.
   */
  private BufferedReader reader;

  /**
   * The {@link PrintWriter} used to wrap the supplied {@link OutputStream}.
   */
  private PrintWriter writer;

  /**
   * Initialise this modem.
   * 
   * @param in
   *          The modem's {@link InputStream}.
   * @param out
   *          The modem's {@link OutputStream}.
   * @param charset
   *          The {@link Charset} used by the modem.
   * @throws IOException
   *           Thrown if there are any I/O issues.
   */
  public void initialise(InputStream in, OutputStream out, Charset charset) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, charset));
    setReader(reader);
    setWriter(writer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String readLine() throws IOException {
    return getReader().readLine();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeLine(String command) throws IOException {
    PrintWriter writer = getWriter();
    writer.println(command);
    writer.flush();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws IOException
   */
  @Override
  public void close() throws IOException {
    try {
      getReader().close();
    }
    finally {
      getWriter().close();
    }
  }

  /**
   * Gets the {@link BufferedReader} used to wrap the supplied
   * {@link InputStream}.
   * 
   * @return the {@link BufferedReader} used to wrap the supplied
   *         {@link InputStream}
   */
  public BufferedReader getReader() {
    return reader;
  }

  /**
   * Sets the {@link BufferedReader} used to wrap the supplied
   * {@link InputStream}.
   * 
   * @param reader
   *          the new {@link BufferedReader} used to wrap the supplied
   *          {@link InputStream}
   */
  public void setReader(BufferedReader reader) {
    this.reader = reader;
  }

  /**
   * Gets the {@link PrintWriter} used to wrap the supplied {@link OutputStream}
   * .
   * 
   * @return the {@link PrintWriter} used to wrap the supplied
   *         {@link OutputStream}
   */
  public PrintWriter getWriter() {
    return writer;
  }

  /**
   * Sets the {@link PrintWriter} used to wrap the supplied {@link OutputStream}
   * .
   * 
   * @param writer
   *          the new {@link PrintWriter} used to wrap the supplied
   *          {@link OutputStream}
   */
  public void setWriter(PrintWriter writer) {
    this.writer = writer;
  }

}
