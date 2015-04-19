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

import scala.collection.JavaConversions._
import scala.reflect.ClassTag
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers
import javax.servlet.Servlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.MediaType
import legacy.remote.google.UrlWithParameters._
import java.net.ServerSocket
import resource.managed

/**
 * @author alex
 *
 */
class JerseyGoogleRequestServiceTest extends FunSuite with ShouldMatchers with GivenWhenThen {

  test("Receive a token request from Google.") {
    ("""
        { "access_token" : "myAccessToken",
          "token_type" : "myTokenType",
          "refresh_token" : "myRefreshToken",
          "expires_in" : 3600,
          "scope" : "myScope",
          "token_id" : "myId"
        }
        """ ->
      ((url: String) =>
        new JerseyGoogleRequestService().
          sendTokenPostRequest(url, Map("one" -> "a", "two" -> "b")))).expect(
        expectedMethod = "POST", expectedParameters = Map("one" -> "a", "two" -> "b"),
        expectedContentType = Some(MediaType.APPLICATION_FORM_URLENCODED),
        expectedResponse =
          TokenResponse("myAccessToken", Some(3600), Some("myRefreshToken")))
  }

  test("Receive a user profile from Google.") {
    ("""
     {
       "id": "xx",
       "name": "Freddie Mercury",
       "email" : "freddie.mercury@gmail.com",
       "given_name": "xx",
       "family_name": "xx",
       "link": "xx",
       "picture": "xx",
       "gender": "xx",
       "locale": "xx"
     }""" ->
      ((url: String) =>
        new JerseyGoogleRequestService().
          sendProfileGetRequest(url.withParameters("one" -> "a", "two" -> "b")))).expect(
        expectedMethod = "GET",
        expectedParameters = Map("one" -> "a", "two" -> "b"),
        expectedContentType = None,
        expectedResponse = UserInfo("freddie.mercury@gmail.com", "Freddie Mercury"))
  }

  implicit class TestCase[M](bf: Pair[String, String => M])(implicit m: ClassTag[M]) {

    def expect(expectedMethod: String, expectedParameters: Map[String, String], expectedContentType: Option[String], expectedResponse: M) = {
      val bodyContent = bf._1
      val expectedResponseGenerator = bf._2
      val port = managed(new ServerSocket(0)).acquireAndGet(_.getLocalPort())
      val server = new Server(port)
      val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
      val servlet = new TestServlet(bodyContent)
      context.setContextPath("/");
      server.setHandler(context);
      val servletHolder = new ServletHolder(servlet);
      servletHolder.setInitOrder(0);
      context.addServlet(servletHolder, "/*");
      try {
        server.start()
        val response = expectedResponseGenerator(s"http://localhost:${port}/request")
        When("receiving a web request")
        Then("the method should be correct")
        servlet.method should equal(expectedMethod)
        Then("the supplied parameters should be recieved")
        servlet.parameters should equal(expectedParameters)
        expectedContentType.map { expectedContentType =>
          Then("the MIME type of the body should be correct")
          servlet.contentType should equal(expectedContentType)
        }
        Then("the response should be as expected")
        response should equal(expectedResponse)
      } finally {
        server.stop()
      }

    }
  }

  class TestServlet(responseBody: String) extends HttpServlet {

    var method: String = _
    var parameters: Map[String, String] = _
    var contentType: String = _

    override def service(req: HttpServletRequest, resp: HttpServletResponse) {
      method = req.getMethod()
      parameters = req.getParameterMap().toMap.mapValues(_(0))
      contentType = req.getHeader("Content-Type")
      resp.setContentType(MediaType.APPLICATION_JSON)
      resp.setContentLength(responseBody.length)
      val writer = resp.getWriter()
      writer.print(responseBody)
      writer.close()
    }
  }
}