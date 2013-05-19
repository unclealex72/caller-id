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

package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.Request
import views.html.defaultpages.unauthorized
import play.api.mvc.Result

/**
 * The controller for the received calls web service.
 * @author alex
 *
 */
class WebService extends Controller {

  val REALM = """Basic realm="Caller ID""""

  def callReceived = Action { request =>
    request.headers.get(AUTHORIZATION) match {
      case Some(authHeader) => {
        val auth = authHeader.substring(6)
        val decodedAuth = new sun.misc.BASE64Decoder().decodeBuffer(auth)
        val credStrings = new String(decodedAuth, "UTF-8").split(":").toList
        if (credStrings.toList != List("username", "password"))
          Forbidden
        else
          Ok
      }
      case None => Unauthorized.withHeaders(WWW_AUTHENTICATE -> REALM)
    }
  }
}