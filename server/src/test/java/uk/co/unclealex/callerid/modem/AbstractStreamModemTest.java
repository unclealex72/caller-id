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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @author alex
 *
 */
public class AbstractStreamModemTest {

  @Test(timeout=1000)
  public void testRead() throws IOException {
    String data = "Hello\nGoodbye\nWait\n";
    Modem modem = createModem(new ByteArrayInputStream(data.getBytes()), new ByteArrayOutputStream());
    List<String> actualData = Lists.newArrayList();
    String line;
    while ((line = modem.readLine()) != null) {
      actualData.add(line);
    }
    Assert.assertEquals("The wrong data was read.", "Hello, Goodbye, Wait", Joiner.on(", ").join(actualData));
  }

  @Test
  public void testWrite() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Modem modem = createModem(new ByteArrayInputStream(new byte[0]), out);
    modem.writeLine("Hello");
    modem.writeLine("Goodbye");
    modem.writeLine("Wait");
    String actualData = new String(out.toString(Charset.defaultCharset().name()));
    Assert.assertEquals("The wrong data was written.", "Hello\nGoodbye\nWait\n", actualData);
  }
  
  protected Modem createModem(InputStream in, OutputStream out) throws IOException {
    return new TestModem(in, out);
  }
  
  class TestModem extends AbstractStreamModem {
    
    public TestModem(InputStream in, OutputStream out) throws IOException {
      initialise(in, out, Charset.defaultCharset());
    }
    
    @Override
    public void initialise() throws IOException {
      // Do nothing.
    }
  }
}
