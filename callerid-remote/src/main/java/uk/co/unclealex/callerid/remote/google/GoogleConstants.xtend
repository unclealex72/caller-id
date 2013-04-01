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

/**
 * An interface for classes that contain the constants used to talk to Google. These constants are abstracted out
 * for testing purposes.
 */
interface GoogleConstants {
    
    /**
     * Get the URL used to get OAuth tokens from Google.
     * @return The URL used to get OAuth tokens from Google.
     */
    def String getOauthTokenUrl()
    
    /**
     * Get the URL used to get contacts from Google.
     * @return The URL used to get contacts from Google.
     */
    def String getContactFeedUrl()
    
    /**
     * Get the amount of time (in milliseconds) before expiry that an access token should be rerequested.
     * @return The amount of time (in milliseconds) before expiry that an access token should be rerequested.
     */
    def Long getTokenExpiryTimeout()
 
}