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

import java.nio.charset.Charset
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.InetAddress

/**
 * @author alex
 *
 */
class NetworkDevice(socket: Socket, charset: Charset) extends AbstractStreamDevice(socket.getInputStream(), socket.getOutputStream(), charset) {

  def this(
    address: InetAddress = InetAddress.getLoopbackAddress(),
    port: Int,
    charset: Charset = Charset.forName("UTF-8")) {
    this(new Socket(address, port), charset)
  }

  override def close = {
    socket.close()
  }
}