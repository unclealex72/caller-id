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

package uk.co.unclealex.callerid.remote.google

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.eclipse.jetty.server.Server
import com.level3.uk.test.server.FreePortFinder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.scalatest.GivenWhenThen
import javax.servlet.Servlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import scala.collection.JavaConversions._
import javax.ws.rs.core.MediaType

/**
 * @author alex
 *
 */
class JerseyGoogleRequestServiceTest extends FunSuite with ShouldMatchers with GivenWhenThen {

  test("Receive a token request from Google.") {
    val port = FreePortFinder.findFreePort()
    val server = new Server(port)
    val context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    val parameters = Map(
      "one" -> "a",
      "two" -> "b")
    val servlet: Servlet = new HttpServlet() {
      override def service(req: HttpServletRequest, resp: HttpServletResponse) {
        When("receiving a web request")
        Then("the method should be a POST")
        req.getMethod() should equal("POST")
        Then("the supplied parameters should be recieved")
        req.getParameterMap().mapValues(_(0)).toList.sorted should equal(parameters.toList.sorted)
        Then("the MIME type of the body should be correct")
        req.getHeader("Content-Type") should equal(MediaType.APPLICATION_FORM_URLENCODED)
        resp.setContentType(MediaType.APPLICATION_JSON)
        val responseBody = """
        { "access_token" : "myAccessToken",
          "token_type" : "myTokenType",
          "refresh_token" : "myRefreshToken",
          "expires_in" : 3600,
          "scope" : "myScope"
        }
        """
        resp.setContentLength(responseBody.length)
        val writer = resp.getWriter()
        writer.print(responseBody)
        writer.close()
      }
    }
    context.setContextPath("/");
    server.setHandler(context);
    val servletHolder = new ServletHolder(servlet);
    servletHolder.setInitOrder(0);
    context.addServlet(servletHolder, "/*");
    try {
      server.start()
      val tokenResponse =
        new JerseyGoogleRequestService().sendRequest(s"http://localhost:${port}/request", parameters)
      Then("a reponse token should be returned")
      tokenResponse should equal(
        TokenResponse(
          accessToken = "myAccessToken", tokenType = "myTokenType",
          refreshToken = Some("myRefreshToken"), expiresInSeconds = Some(3600), scope = Some("myScope")))
    } finally {
      server.stop()
    }
  }
}