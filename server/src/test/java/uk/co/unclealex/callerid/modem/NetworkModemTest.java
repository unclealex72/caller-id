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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

/**
 * @author alex
 *
 */
public class NetworkModemTest {

  ServerSocket modemServerSocket;
  Modem modem;
  
  @Before
  public void setup() throws IOException {
    modemServerSocket = new ServerSocket(0);
    modem = new NetworkModem(modemServerSocket.getLocalPort(), Charset.defaultCharset());
    modem.initialise();
  }
  
  @Test(timeout=1000)
  public void testRead() throws IOException, InterruptedException, ExecutionException {
    Callable<Void> callable = new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        Socket socket = modemServerSocket.accept();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.println("Freddie");
        writer.println("Brian");
        writer.flush();
        socket.close();
        return null;
      }
    };
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Void> future = executor.submit(callable);
    List<String> actualData = Lists.newArrayList();
    String line;
    while ((line = modem.readLine()) != null) {
      actualData.add(line);
    }
    Assert.assertEquals("The wrong data was read.", "Freddie, Brian", Joiner.on(", ").join(actualData));
    future.get();
  }

  @Test(timeout=1000)
  public void testWrite() throws Exception {
    modem.writeLine("Brian");
    modem.writeLine("Freddie");
    Socket socket = modemServerSocket.accept();
    modem.close();
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    List<String> actualData = Lists.newArrayList();
    String line;
    while ((line = reader.readLine()) != null) {
      actualData.add(line);
    }
    Assert.assertEquals("The wrong data was written.", "Brian, Freddie", Joiner.on(", ").join(actualData));
  }

  @After
  public void teardown() {
    Closeables.closeQuietly(modemServerSocket);
  }

}
