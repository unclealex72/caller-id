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

import com.google.api.client.auth.oauth2.TokenResponse
import com.google.common.base.Optional
import com.google.common.collect.Lists
import com.google.common.io.Closeables
import com.google.gdata.util.common.base.Charsets
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InputStreamReader
import java.io.Reader
import java.util.Date
import java.util.List
import javax.inject.Inject
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import uk.co.unclealex.callerid.remote.model.OauthToken
import uk.co.unclealex.callerid.remote.model.OauthTokenType
import uk.co.unclealex.callerid.remote.model.User

import static extension uk.co.unclealex.xtend.OptionalExtensions.*

/**
 * The default implementation of {@link GoogleTokenService}.
 */
class GoogleTokenServiceImpl implements GoogleTokenService {
    
    /**
     * The Google configuration object used to configure how to get contacts from Google.
     */
    @Property val GoogleConfiguration googleConfiguration

    /**
     * The Google constants object used to configure how to get contacts from Google.
     */
    @Property val GoogleConstants googleConstants

    /**
     * The service used to get the current time.
     */
    @Property val NowService nowService

    @Inject
    new(GoogleConfiguration googleConfiguration, GoogleConstants googleConstants, NowService nowService) {
        this._googleConfiguration = googleConfiguration
        this._googleConstants = googleConstants
        this._nowService = nowService
    }

    /**
     * Find a user's token by it's token type.
     * @param user The user to interrogate.
     * @param oauthTokenType the token type to search for.
     * @return A token of the given type or null if no such token exists.
     */
    def Optional<OauthToken> findTokenByType(User user, OauthTokenType oauthTokenType) {
        user.oauthTokens.findFirst [
            tokenType.equals(oauthTokenType)
        ].asOptional
    }

    /**
     * Find or create a user's token by it's token type.
     * @param user The user to interrogate.
     * @param oauthTokenType the token type to search for.
     * @return A token of the given type or a new token with the given type if none such exists.
     */
    def OauthToken findTokenByTypeOrCreate(User user, OauthTokenType tokenType) {
        user.findTokenByType(tokenType) ?: (
            new OauthToken => [
            it.tokenType = tokenType
            user.oauthTokens.add(it)
        ]
        )
    }

    /**
     * Get the user's access token, refreshing it if neccessary.
     * @param user The user whose token is being looked for.
     * @param A valid access token.
     */
    def String accessToken(User user) {
        val Optional<OauthToken> optionalAccessToken = user.findTokenByType(OauthTokenType::ACCESS)
        val OauthToken accessToken = optionalAccessToken.or(
            new OauthToken => [
                tokenType = OauthTokenType::ACCESS
                user.oauthTokens.add(it)
            ]
        )
        if (accessToken.expired) {
            user.updateAccessToken(accessToken)
        }
        accessToken.token
    }

    /**
     * Determine whether an access token has expired.
     * @param oauthToken The token to check.
     * @return True if the token's expiry date is soon to expire, expired or non existent, false otherwise.
     */
    def boolean expired(OauthToken oauthToken) {
        oauthToken.expiryDate.transform([time - googleConstants.tokenExpiryTimeout < nowService.now]).or(false)
    }

    /**
     * Request a an OAuth token from Google.
     * @param tokenType The type of token to request.
     * @param token The user's current refresh token.
     * @param grantType The grant_type to send to Google with this request.
     * @param includeRedirect True if a redirect URI should be included in the request, false otherwise.
     * @return The {@link TokenResponse} received from Google.
     */
    def TokenResponse requestToken(String tokenType, String token, String grantType, boolean includeRedirect) {
        val HttpClient client = new DefaultHttpClient()
        val HttpPost post = new HttpPost(googleConstants.oauthTokenUrl)
        val List<BasicNameValuePair> formparams = Lists::newArrayList(
            "client_id" -> googleConfiguration.consumerId,
            "client_secret" -> googleConfiguration.consumerSecret,
            tokenType -> token,
            "grant_type" -> grantType
        ).map[new BasicNameValuePair(key, value)]
        if (includeRedirect) {
            formparams.add(new BasicNameValuePair("redirect_uri", "urn:ietf:wg:oauth:2.0:oob"))
        }
        val UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Charsets::UTF_8)
        post.entity = entity
        val HttpResponse response = client.execute(post)
        val int statusCode = response.statusLine.statusCode
        if (statusCode != 200) {
            throw new GoogleAuthenticationFailedException(
                "Requesting a token refresh resulted in a http status of " + statusCode)
        }
        val Reader reader = new InputStreamReader(response.entity.content, Charsets::UTF_8)
        try {
            val Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy::LOWER_CASE_WITH_UNDERSCORES).
                create()
            gson.fromJson(reader, typeof(TokenResponse))
        } finally {
            Closeables::closeQuietly(reader)
        }
    }

    /**
     * Update a user's access token.
     * @param user The user whose access token needs updating.
     * @param accessToken The user's current access token.
     */
    def void updateAccessToken(User user, OauthToken userAccessToken) {
        val Optional<OauthToken> refreshToken = user.findTokenByType(OauthTokenType::REFRESH)
        refreshToken.optionalIf(
            [
                requestToken("refresh_token", token, "refresh_token", false) => [
                    userAccessToken.token = accessToken userAccessToken.expiryDate = expiryDate]],
            [|throw new GoogleAuthenticationFailedException("No refresh token found.")])
    }

    override installSuccessCode(User user, String successCode) {
        val OauthToken userAccessToken = user.findTokenByTypeOrCreate(OauthTokenType::ACCESS)
        val OauthToken userRefreshToken = user.findTokenByTypeOrCreate(OauthTokenType::REFRESH)
        requestToken("code", successCode, "authorization_code", true) => [
            userAccessToken.token = accessToken
            userAccessToken.expiryDate = expiryDate
            userRefreshToken.token = refreshToken
        ]
    }

    /**
     * Calculate the expiry date of a Google {@link TokenResponse}.
     * @param tokenResponse the token response from Google.
     * @return The date and time the token expires.
     */
    def Optional<Date> expiryDate(TokenResponse tokenResponse) {
        tokenResponse.expiresInSeconds.asOptional.transform([new Date(it * 1000 + nowService.now)])
    }

}