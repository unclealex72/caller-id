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

package uk.co.unclealex.callerid.call;

/**
 * An interface for classes that provide the configuration required to connect
 * to the remote server via HTTPS.
 *
 * @author alex
 *
 */
case class RemoteConfiguration(
  /**
   * The URL of the remote REST server.
   */
  url: String,
  /**
   * The username used to authenticate against the remote REST server
   */
  username: String,
  /**
   * The password used to authenticate against the remote REST server
   */
  password: String) {
}
