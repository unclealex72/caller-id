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

import com.google.gdata.client.Service$GDataRequest$RequestType
import com.google.gdata.util.ContentType
import com.google.gdata.util.ServiceException
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL

import static extension uk.co.unclealex.callerid.remote.google.UrlWithParameters.*
import com.google.gdata.client.Service$GDataRequest

/**
 * An extension to Google's contact service that adds OAuth authentication as well as making sure that
 * results are not paged.
 */
class ContactsService extends com.google.gdata.client.contacts.ContactsService {

    /**
     * The value of the OAuth token used to authorise this request.
     */
    @Property val String oauthToken

    /**
     * Create a new Contacts service.
     * @param applicationName the name of the application making the request.
     * @param oauthToken The value of the OAuth token used to authorise this request.
     */
    new(String applicationName, String oauthToken) {
        super(applicationName);
        this._oauthToken = oauthToken;
    }

    override GDataRequest createRequest(RequestType type, URL requestUrl, ContentType contentType) throws IOException,
			ServiceException {
        super.createRequest(type, authorise(requestUrl), contentType);
    }

    /**
     * Authorise a URL by adding an <i>oauth_token</i> parameter and a <i>max-results</i> parameter
     */
    def URL authorise(URL url) throws MalformedURLException, UnsupportedEncodingException {
        url.parse.withParameters("oauth_token" -> oauthToken, "max-results" -> Integer::MAX_VALUE).toURL;
    }
}
