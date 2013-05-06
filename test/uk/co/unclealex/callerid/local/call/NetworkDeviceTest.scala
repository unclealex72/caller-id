/**
 * Copyright 2013 Alex Jones
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
package uk.co.unclealex.callerid.local.call

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import com.google.common.base.Charsets

import uk.co.unclealex.callerid.local.device.NetworkDevice

class NetworkDeviceTest extends FunSuite with ShouldMatchers {

  test("A network device can read and write to a network") {
    val echoServer = new EchoServer
    new Thread(echoServer).start
    val networkDevice = new NetworkDevice(port = echoServer.port);
    networkDevice writeLine "Hello"
    val response = networkDevice.readLine
    response should equal(Some("Hello"))
    networkDevice.close
  }
}

/**
 * A small server that echoes whatever it is sent to it.
 */
class EchoServer extends Runnable {

  val serverSocket = new ServerSocket(0)

  override def run = {
    val socket = serverSocket.accept
    val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8), true)
    val in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charsets.UTF_8))
    Stream.continually(in.readLine()).takeWhile(_ != null).foreach(out.println(_))
    List(in, out, socket).foreach(c => c.close)
  }

  def port: Int = serverSocket.getLocalPort();
}
