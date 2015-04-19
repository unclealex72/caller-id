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
package legacy.remote.google

import scala.collection.Map
import scala.reflect.ClassTag

/**
 * An interface allowing the abstraction of requesting receiving Google Json token objects.
 */
trait GoogleRequestService {

  /**
   * Send a form POST request to Google.
   * @param url The google URL to call
   * @param formParameters The form parameters to send.
   * @return The response object from Google.
   */
  def sendTokenPostRequest(url: String, formParameters: Map[String, String]): TokenResponse

  /**
   * Send a form GET request to Google.
   * @param urlWithParameters The google URL to call
   * @return The response object from Google.
   */
  def sendProfileGetRequest(urlWithParameters: UrlWithParameters): UserInfo
}
