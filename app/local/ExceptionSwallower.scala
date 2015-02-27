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

package local

import com.typesafe.scalalogging.slf4j.Logging

/**
 * A helper trait for swallowing and reporting on exceptions.
 * @author alex
 *
 */
object ExceptionSwallower extends Logging {

  /**
   * Swallow and continue from any exceptions.
   * @param message The message to use when logging the thrown exception.
   * @param block The code to execute.
   * @return true if the code executed successfully or false if an exception was thrown.
   */
  def swallow(message: String)(block: => Unit): Boolean = {
    try {
      block
      true
    } catch {
      case e: Exception => {
        logger.error(message, e)
        false
      }
    }
  }
}