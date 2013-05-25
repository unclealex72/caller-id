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
 * @author unclealex72
 *
 */

package uk.co.unclealex.callerid.local.device

import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.io.OutputStreamWriter
import java.io.DataOutputStream
import java.io.DataInputStream

/**
 * A IO device based on a socket.
 * @author alex
 *
 */
class SocketIoDevice(host: String, port: Int) extends IoDevice {

  val socket = new Socket(host, port)
  val in = new DataInputStream(socket.getInputStream())
  val out = new DataOutputStream(socket.getOutputStream())

  def readLine: Option[String] = Option(in.readLine())

  def writeLine(line: String) = {
    out.writeUTF(line)
    out.write(13)
  }

  def close = socket close
}