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

package uk.co.unclealex.callerid.local.call

import org.specs2.mutable.Specification
import java.net.ServerSocket
import org.eclipse.jetty.server.Server
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import scala.collection.immutable.Stream
import scala.io.Source
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

/**
 * @author alex
 *
 */
class JerseyCallAlerterTest extends Specification {

  "The Jersey based call alerter" should {
    val socket = new ServerSocket(0)
    val port = socket.getLocalPort()
    socket close
    val server = new Server(port)
    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    val servlet = new TestServlet("OK")
    context.setContextPath("/")
    server.setHandler(context)
    val servletHolder = new ServletHolder(servlet)
    servletHolder.setInitOrder(0)
    context.addServlet(servletHolder, "/*")
    server.start
    val callAlerter = new JerseyCallAlerter(new RemoteConfiguration(s"http://localhost:$port/go", "username", "password"))
    val actualResponse = callAlerter callMade "NUMBER"
    "use POST" in {
      servlet.method must be equalTo "POST"
    }
    "send the correct authorisation header" in {
      servlet.authorisationHeader must be equalTo Some("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
    }
    "go to the correct URL" in {
      servlet.url must be equalTo "/go"
    }
    "send the correct body" in {
      servlet.body must be equalTo "NUMBER"
    }
    "return the correct response" in {
      actualResponse must be equalTo "OK"
    }
    server stop
  }
}

class TestServlet(response: String) extends HttpServlet {

  var method: String = _
  var url: String = _
  var authorisationHeader: Option[String] = _
  var body: String = _

  override def service(req: HttpServletRequest, res: HttpServletResponse): Unit = {
    method = req.getMethod()
    url = req.getRequestURI()
    authorisationHeader = Option(req.getHeader("Authorization"))
    body = Source.fromInputStream(req.getInputStream()).mkString
    res.setContentType("text/plain")
    val writer = res.getWriter
    writer print response
    writer close
  }
}