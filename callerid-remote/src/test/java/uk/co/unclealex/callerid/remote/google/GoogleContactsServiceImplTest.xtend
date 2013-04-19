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
 * @author alex
 *
 */
package uk.co.unclealex.callerid.remote.google

import com.google.common.io.ByteStreams
import com.level3.uk.test.server.FreePortFinder
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.MediaType
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.junit.BeforeClass
import org.junit.Test
import uk.co.unclealex.callerid.remote.model.User

import static extension org.mockito.Mockito.*

/**
 * Test that, given a predefined set of contacts, they are correctly read by the Google Contacts Service.
 */
class GoogleContactsServiceImplTest {
    
    static var Iterable<GoogleContact> googleContacts
    
    @BeforeClass
    def static void loadContacts() {
        val int port = FreePortFinder::findFreePort        
        val Server server = new Server(port)
        val ServletHolder servletHolder = new ServletHolder(new ContactsServlet);
        server.handler = new ServletContextHandler(ServletContextHandler::SESSIONS) => [
            contextPath ="/"
            addServlet(servletHolder, "/*")
        ]
        try {
            server.start
            val User user = new User
            val GoogleTokenService mockGoogleTokenService = typeof(GoogleTokenService).mock
            when(mockGoogleTokenService.accessToken(user)).thenReturn("")
            val GoogleConstants mockGoogleConstants = typeof(GoogleConstants).mock
            when(mockGoogleConstants.contactFeedUrl).thenReturn('''http://localhost:«port»/''')
            googleContacts = new GoogleContactsServiceImpl(mockGoogleTokenService, mockGoogleConstants).getAllContacts(user)
        }
        finally {
            server.stop
        }
    }
    
    @Test
    def void test() {
        println(googleContacts)
    }
}

class ContactsServlet extends HttpServlet {
    
    override protected service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(MediaType::APPLICATION_ATOM_XML)
        ByteStreams::copy([|class.classLoader.getResourceAsStream("contacts.xml")], [|resp.outputStream])
    }
    
}