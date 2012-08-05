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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author alex
 *
 */
public class SqueezeboxCliTest {

  @Test
  public void testCountPlayers() throws IOException {
    Squeezebox squeezebox = mock(Squeezebox.class);
    when(squeezebox.execute("player count ?")).thenReturn("2");
    @SuppressWarnings("resource")
    SqueezeboxCli squeezeboxCli = new SqueezeboxCliImpl(squeezebox);
    Assert.assertEquals("The wrong number of players were returned.", 2, squeezeboxCli.countPlayers());
  }

  @Test
  public void testDisplay() throws IOException {
    Squeezebox squeezebox = mock(Squeezebox.class);
    @SuppressWarnings("resource")
    SqueezeboxCli squeezeboxCli = new SqueezeboxCliImpl(squeezebox);
    when(squeezebox.execute("player id 0 ?")).thenReturn("00:11");
    squeezeboxCli.display(0, "Top Line", "Bottom Line!", 30);
    verify(squeezebox).execute("player id 0 ?");
    verify(squeezebox).execute("00:11 display Top%20Line Bottom%20Line%21 30");
  }
}
