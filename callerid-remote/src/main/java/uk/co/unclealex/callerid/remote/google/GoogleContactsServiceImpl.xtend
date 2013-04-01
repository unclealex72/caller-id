/**
 * Copyright 2010 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance
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

import com.google.api.client.auth.oauth2.TokenResponse
import com.google.common.base.Charsets
import com.google.common.base.Joiner
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.google.common.io.Closeables
import com.google.gdata.data.contacts.ContactEntry
import com.google.gdata.data.contacts.ContactFeed
import com.google.gdata.data.extensions.PostalAddress
import com.google.gdata.data.extensions.StructuredPostalAddress
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.util.List
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.eclipse.xtext.xbase.lib.Functions
import org.eclipse.xtext.xbase.lib.Pair
import org.springframework.transaction.annotation.Transactional
import uk.co.unclealex.callerid.remote.model.OauthToken
import uk.co.unclealex.callerid.remote.model.OauthTokenType
import uk.co.unclealex.callerid.remote.model.User
import java.util.Date

@Transactional
class GoogleContactsServiceImpl implements GoogleContactsService {

    /**
     * The Google configuration object used to configure how to get contacts from Google.
     */
    @Property var GoogleConfiguration googleConfiguration

    /**
     * The Google constants object used to configure how to get contacts from Google.
     */
    @Property var GoogleConstants googleConstants
    
    /**
     * The service used to get the current time.
     */
    @Property var NowService nowService
    
    def ContactsService createContactsService(User user) {
        return new ContactsService("callerid.unclealex.co.uk", user.accessToken)
    }

    override getAllContacts(User user) {
        val ContactsService contactsService = createContactsService(user)
        val ContactFeed resultFeed = contactsService.getFeed(
            new URL(googleConstants.contactFeedUrl), typeof(ContactFeed))
        Sets::newHashSet(
            resultFeed.entries.filter[hasName].map [
                new GoogleContact(name.fullName.value, findPostalAddress,
                    Sets::newHashSet(phoneNumbers.map[phoneNumber]))
            ])
    }

    def String findPostalAddress(ContactEntry entry) {
        val List<Pair<String, Boolean>> addresses = newArrayList()
        addresses.addAll(entry.postalAddresses.nullSafeMap[toAddress])
        addresses.addAll(entry.structuredPostalAddresses.nullSafeMap[toAddress])
        val List<Pair<String, Boolean>> primaryAddresses = Lists::newArrayList(addresses.filter[value])
        val List<Pair<String, Boolean>> addressesToUse = #[primaryAddresses, addresses].findFirst[!it.empty]
        if (addressesToUse != null) addressesToUse.get(0).key
    }
    
    def Pair<String, Boolean> toAddress(PostalAddress postalAddress) {
        postalAddress.value -> postalAddress.primary
    }

    def Pair<String, Boolean> toAddress(StructuredPostalAddress spa) {
        val address = Joiner::on(' ').skipNulls.join(
            if (spa.hasPobox) spa.pobox.value, 
            if (spa.hasHousename) spa.housename.value,
            if (spa.hasStreet) spa.street.value, 
            if (spa.hasSubregion) spa.subregion.value, 
            if (spa.hasRegion) spa.region.value, 
            if (spa.hasCity) spa.city.value, 
            if (spa.hasPostcode) spa.postcode.value, 
            if (spa.hasCountry) spa.country)
        address -> if (spa.hasPrimary) spa.primary else null
    }

    def <E, F> List<F> nullSafeMap(List<E> list, Functions$Function1<E, F> transformer) {
        (list ?: newArrayList()).map(transformer)
    }
    
    def OauthToken findTokenByType(User user, OauthTokenType oauthTokenType) {
        user.oauthTokens.findFirst [
            tokenType.equals(oauthTokenType)
        ]
    }

    def OauthToken findTokenByTypeOrCreate(User user, OauthTokenType tokenType) {
        user.findTokenByType(tokenType) ?: (
            new OauthToken => [ oauthToken |
            oauthToken.tokenType = tokenType
            user.oauthTokens.add(oauthToken)
        ]
        )
    }

    def String accessToken(User user) {
        var OauthToken accessToken = user.findTokenByType(OauthTokenType::ACCESS)
        if (accessToken == null ||
            accessToken.getExpiryDate().getTime() - googleConstants.tokenExpiryTimeout < nowService.now) {
            if (accessToken == null) {
                accessToken = new OauthToken => [tokenType = OauthTokenType::ACCESS]
                user.oauthTokens.add(accessToken)
            }
            user.updateAccessToken(accessToken)
        }
        return accessToken.getToken()
    }

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
        val UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8")
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
     * @param accessToken
     * @throws GoogleAuthenticationFailedException 
     * @throws IOException 
     */
    def void updateAccessToken(User user, OauthToken userAccessToken) {
        val OauthToken refreshToken = user.findTokenByType(OauthTokenType::REFRESH)
        if (refreshToken == null) {
            throw new GoogleAuthenticationFailedException("No refresh token found.")
        }
        requestToken("refresh_token", refreshToken.getToken(), "refresh_token", false) => [
            userAccessToken.token = accessToken
            userAccessToken.expiryDate = expiryDate
        ]
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

    def Date expiryDate(TokenResponse tokenResponse) {
        if (tokenResponse.expiresInSeconds != null) new Date(tokenResponse.expiresInSeconds * 1000 + nowService.now)
    }
}
