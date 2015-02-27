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

package local.device

import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * An IO device that uses data streams to read and write to a device.
 * @author alex
 *
 */
class DataStreamIoDevice(io: Io) extends IoDevice {

  val in = new DataInputStream(io in)
  val out = new DataOutputStream(io out)

  override def readLine = Option(in readLine ())

  override def writeLine(line: String) = {
    out writeUTF line
    out write 13
  }

  override def close = io close
}