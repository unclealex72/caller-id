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
package uk.co.unclealex.callerid.remote.dao

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.api.json.JSONConfiguration
import com.sun.jersey.core.util.MultivaluedMapImpl
import java.util.Collections
import java.util.Map
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import uk.co.unclealex.callerid.remote.google.GoogleRequestService

/**
 * An implementation of {@link GoogleRequestService} that uses Jersey to serialise and deserialise Google requests.
 */
class JerseyGoogleRequestService implements GoogleRequestService {

    override <R> sendRequest(String url, Map<String, String> formParameters, Class<R> responseType) {
        val Client client = Client::create(new DefaultClientConfig => [
            it.features.put(JSONConfiguration::FEATURE_POJO_MAPPING, true)
        ])
        val MultivaluedMap<String, String> formParams = new MultivaluedMapImpl
        formParameters.forEach[key, value|
            formParams.put(key, Collections::singletonList(value))
        ]
        client.resource(url).type(MediaType::APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType::APPLICATION_JSON_TYPE).
            post(responseType, formParams)
    }

}
