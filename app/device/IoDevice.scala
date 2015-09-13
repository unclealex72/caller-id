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

package device

import java.io.Closeable

import scala.collection.immutable.Stream

/**
 * A trait to allow the abstraction of getting an input stream and output stream to talk to a networked device.
 * @author alex
 *
 */
trait IoDevice extends Closeable {

  /**
   * Get the input stream for this IO device.
   */
  def readLine: Option[String]

  /**
   * Get the output stream for this IO device.
   */
  def writeLine(line: String): Unit

  def readLines: Stream[String] =
    Stream continually readLine takeWhile (_.isDefined) map (_.get.trim)

}