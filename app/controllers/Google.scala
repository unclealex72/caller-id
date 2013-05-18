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

package controllers;

import javax.inject.Inject
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.EssentialAction
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.mvc.Security
import uk.co.unclealex.callerid.remote.google.GoogleTokenService
import uk.co.unclealex.callerid.remote.google.GoogleUser
import uk.co.unclealex.callerid.remote.model.CallerIdSchema.inTransaction

/**
 * A controller that uses Google OAuth to provide authentication to other
 * controllers.
 *
 * @author alex
 *
 */
class Google @Inject() (
  /**
   * The service used to parse google OAuth tokens.
   */
  googleTokenService: GoogleTokenService) extends Controller {

  def callback =
    Action { request =>
      def onSuccess = (successCode: String) =>
        inTransaction { googleTokenService.userOf(successCode) }.map { user =>
          Redirect(routes.Application.index).withSession("user" -> user.serialise)
        }.getOrElse {
          Forbidden("You are not allowed access to this resource.")
        }
      request.getQueryString("code") map onSuccess getOrElse {
        BadRequest("I do not understand what you are trying to say.")
      }
    }

  def login = Action {
    Redirect(googleTokenService.loginPage)
  }
}
/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def user(request: RequestHeader) =
    request.session.get("user").flatMap(GoogleUser.parse)

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Google.login)

  // --

  /**
   * Action for authenticated users.
   */
  def isAuthenticated(f: => GoogleUser => Request[AnyContent] => Result): EssentialAction =
    Security.Authenticated(user, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }

}
