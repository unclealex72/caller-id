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

package legacy.controllers

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Response
import play.api.mvc.Result
import legacy.remote.call.CallReceivedService
import views.html.defaultpages.badRequest
import legacy.remote.number.NumberFormatter
import javax.inject.Inject
import legacy.remote.model.CallerIdSchema._

/**
 * The controller for the received calls web service.
 * @author alex
 *
 */
class WebService @Inject() (
  /**
   * The service used to register a received call.
   */
  callReceivedService: CallReceivedService,
  /**
   * The service used to pretty-print phone numbers.
   */
  numberFormatter: NumberFormatter,
  /**
   * The class that contains the username and password required to log in to this web service.
   */
  webServiceSecurityConfiguration: WebServiceSecurityConfiguration) extends Controller {

  val REALM = """Basic realm="Caller ID""""

  def callReceived = secure { request =>
    request.body.asText match {
      case Some(phonenumber) => {
        val receivedCall = inTransaction { callReceivedService callReceived phonenumber }
        val message = receivedCall contact match {
          case Some(contact) => contact name
          case None => numberFormatter formatNumber (receivedCall phoneNumber) mkString (" ")
        }
        Ok(message) as "text/plain"
      }
      case None => BadRequest
    }
  }

  def secure(authorised: Request[AnyContent] => Result) = Action { request =>
    request.headers.get(AUTHORIZATION) match {
      case Some(authHeader) => {
        val auth = authHeader.substring(6)
        val decodedAuth = new sun.misc.BASE64Decoder().decodeBuffer(auth)
        val credStrings = new String(decodedAuth, "UTF-8").split(":").toList
        if (credStrings.toList != List(webServiceSecurityConfiguration.username, webServiceSecurityConfiguration.password))
          Forbidden
        else
          authorised(request)
      }
      case None => Unauthorized.withHeaders(WWW_AUTHENTICATE -> REALM)
    }
  }
}