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

package uk.co.unclealex.callerid.remote.google

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.GivenWhenThen
import org.scalamock.scalatest.MockFactory
import java.text.SimpleDateFormat
import uk.co.unclealex.callerid.remote.model.User
import java.util.ArrayList
import scala.collection.JavaConversions._
import uk.co.unclealex.callerid.remote.model.OauthTokenType._
import uk.co.unclealex.callerid.remote.model.OauthTokenType
import uk.co.unclealex.callerid.remote.model.OauthToken
import java.util.Date
/**
 * @author alex
 *
 */
class GoogleTokenServiceImplTest extends FunSuite with ShouldMatchers with GivenWhenThen with MockFactory {

  val googleConstants = new GoogleConstants
  val googleConfiguration = new GoogleConfiguration("shh", "id")

  test("Install a success code") {
    val (nowService, googleRequestService, googleTokenService) = createServices
    (nowService.now _).when().returning("05/09/2014 09:12:00".toTime)
    (googleRequestService.sendRequest _).when(
      googleConstants.oauthTokenUrl,
      Map(
        "client_secret" -> googleConfiguration.consumerSecret,
        "client_id" -> googleConfiguration.consumerId,
        "grant_type" -> "authorization_code",
        "code" -> "success",
        "redirect_uri" -> "urn:ietf:wg:oauth:2.0:oob")).returning(
        new TokenResponse("access", "Bearer", Some(3600), Some("refresh"), None))
    When("installing a success code")
    val user = User("user")
    googleTokenService.installSuccessCode(user, "success")
    Then("the user should have two tokens")
    val oauthTokens = user.oauthTokens
    oauthTokens should have size (2)
    Then("one should be an expiring access token")
    def expectToken(tokenType: OauthTokenType, value: String, expiryDate: Option[Date]) = {
      val expectedToken = tokenType token value expires expiryDate
      oauthTokens.find { tokenType == _.tokenType } should equal(Some(expectedToken))
    }
    expectToken(ACCESS, "access", Some("05/09/2014 10:12:00".toDate))
    Then("the other should be a non-expiring refresh token")
    expectToken(REFRESH, "refresh", None)
  }

  test("Get a non-expired access token") {
    val (nowService, googleRequestService, googleTokenService) = createServices
    (nowService.now _).when().returning("05/09/2014 09:12:00".toTime)
    val currentAccessToken = ACCESS token "access" expires "05/10/2014 15:00:00".toDate
    val user = newUser(currentAccessToken, REFRESH token "refresh")
    When("getting a user's non-expired access token")
    Then("the original access token should be returned")
    googleTokenService.accessToken(user) should equal("access")
    user.oauthTokens.find(ACCESS == _.tokenType) should equal(Some(currentAccessToken))
  }

  test("Get a new access token as the user's current token has expired") {
    val (nowService, googleRequestService, googleTokenService) = createServices
    (nowService.now _).when().returning("05/09/2014 09:12:00".toTime)
    val user = newUser(
      ACCESS token "access" expires "05/10/2013 15:00:00".toDate,
      REFRESH token "refresh")
    (googleRequestService.sendRequest _).when(
      googleConstants.oauthTokenUrl,
      Map(
        "client_secret" -> googleConfiguration.consumerSecret,
        "client_id" -> googleConfiguration.consumerId,
        "refresh_token" -> "refresh",
        "grant_type" -> "refresh_token")).returning(
        new TokenResponse("new access", "Bearer", Some(3600), None, None))
    When("getting an access token as the current token has expired")
    Then("the access token should be the one returned from Google")
    googleTokenService.accessToken(user) should equal("new access")
    Then("the user should be updated to be associated with the new token")
    val accessTokens = user.oauthTokens.filter(ACCESS == _.tokenType)
    accessTokens should have size (1)
    accessTokens.iterator.next should equal(ACCESS token "new access" expires "05/09/2014 10:12:00".toDate)

  }

  def newUser(oauthTokens: OauthToken*) = {
    new User("user", oauthTokens.toBuffer)
  }

  def createServices = {
    val nowService = stub[NowService]
    val googleRequestService = stub[GoogleRequestService]
    (nowService,
      googleRequestService,
      new GoogleTokenServiceImpl(
        googleConfiguration, googleConstants, nowService, googleRequestService))
  }

  implicit class OauthTokenTypeImplicits(oauthTokenType: OauthTokenType) {

    def token(token: String) = {
      new OauthToken(oauthTokenType, Some(token), None)
    }
  }

  implicit class OauthTokenImplicits(oauthToken: OauthToken) {

    def expires(expiryDate: Option[Date]): OauthToken = {
      oauthToken.expiryDate = expiryDate
      oauthToken
    }

    def expires(expiryDate: Date): OauthToken = expires(Some(expiryDate))
  }

  implicit class DateFormatImplicits(date: String) {

    val df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    def toTime = {
      date.toDate.getTime()
    }

    def toDate = {
      df.parse(date)
    }
  }

}