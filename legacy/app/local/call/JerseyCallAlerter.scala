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

package legacy.local.call

import legacy.local.configuration.RemoteConfiguration

import scala.collection.JavaConversions.mapAsScalaMap

import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider

import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.client.apache4.ApacheHttpClient4
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config.PROPERTY_CREDENTIALS_PROVIDER
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config.PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config
import com.typesafe.scalalogging.slf4j.Logging

import javax.inject.Inject
import RemoteConfiguration
import javax.inject.Inject

/**
 * An implementation of {@link CallAlerter} that uses Jersey to communicate to a REST server.
 */
class JerseyCallAlerter @Inject() (remoteConfiguration: RemoteConfiguration) extends CallAlerter with Logging {

  /**
   * The Jersey {@link Client} used to talk to the REST server.
   */
  val client = ApacheHttpClient4.create {
    val credentialsProvider = new CredentialsProvider() {
      def setCredentials(authscope: AuthScope, credentials: Credentials) = {}
      def getCredentials(authscope: AuthScope): Credentials = {
        new UsernamePasswordCredentials(remoteConfiguration.username, remoteConfiguration.password)
      }
      def clear: Unit = {}
    }
    val cc: ClientConfig = new DefaultApacheHttpClient4Config
    cc.getProperties() ++= Map(
      PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION -> Boolean.box(true),
      PROPERTY_CREDENTIALS_PROVIDER -> credentialsProvider)
    cc
  }

  val url: String = remoteConfiguration.url

  override def callMade(number: String) = {
    logger info s"Sending $number to $url"
    val message = client resource (url) post (classOf[String], number)
    logger info s"Received message '$message'"
    message
  }

}
