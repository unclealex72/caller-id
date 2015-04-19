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
import scala.collection.Map
import scala.reflect.ClassTag
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.core.util.MultivaluedMapImpl
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import legacy.remote.json.ScalaObjectMapperProvider
import com.fasterxml.jackson.databind.DeserializationConfig

/**
 * An implementation of {@link GoogleRequestService} that uses Jersey to serialise and deserialise Google requests.
 */
class JerseyGoogleRequestService extends GoogleRequestService {

  class IgnoringScalaObjectMapperProvider {

  }
  val client: Client = {
    val clientConfig = new DefaultClientConfig
    clientConfig.getClasses() += classOf[ScalaObjectMapperProvider]
    Client.create(clientConfig)
  }

  override def sendTokenPostRequest(url: String, formParameters: Map[String, String]): TokenResponse = {
    val formParams: MultivaluedMap[String, String] = new MultivaluedMapImpl
    formParameters.foreach { case (k, v) => formParams.putSingle(k, v) }
    client.resource(url).`type`(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).
      post(classOf[TokenResponse], formParams)
  }

  override def sendProfileGetRequest(urlWithParameters: UrlWithParameters): UserInfo = {
    client.resource(urlWithParameters.toURL.toURI).
      accept(MediaType.APPLICATION_JSON_TYPE).get(classOf[UserInfo])
  }
}
