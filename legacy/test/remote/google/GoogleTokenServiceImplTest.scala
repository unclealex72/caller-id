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

import java.sql.Timestamp
import java.text.SimpleDateFormat
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers
import legacy.remote.google.UrlWithParameters.UrlWithParametersStringImplicits
import legacy.remote.model.User
import legacy.remote.dao.UserDao
/**
 * @author alex
 *
 */
class GoogleTokenServiceImplTest extends FunSuite with ShouldMatchers with GivenWhenThen with MockFactory {

  val googleConstants = new GoogleConstants
  val googleConfiguration = GoogleConfiguration("callback", "shh", "id")

  test("Install a success code for a new user") {
    val (userDao, nowService, googleRequestService, googleTokenService) = createServices
    (nowService.now _).expects().returning("05/09/2014 09:12:00".toTime)
    (userDao.findByEmailAddress _).expects("freddie.mercury@gmail.com").returning(None)
    (googleRequestService.sendTokenPostRequest _).expects(
      googleConstants.oauthTokenUrl,
      Map(
        "client_secret" -> googleConfiguration.consumerSecret,
        "client_id" -> googleConfiguration.consumerId,
        "grant_type" -> "authorization_code",
        "code" -> "success",
        "redirect_uri" -> "callback")).returning(
        TokenResponse("access", Some(3600), Some("refresh")))
    (googleRequestService.sendProfileGetRequest _).expects(
      googleConstants.userProfileUrl.withParameters("access_token" -> "access")).returning(
        UserInfo("freddie.mercury@gmail.com", "Freddie Mercury"))

    (userDao.store _).expects(where {
      (user: User) =>
        user.username == "freddie.mercury@gmail.com" &&
          user.accessToken == "access" &&
          user.expiryDate == "05/09/2014 10:12:00".toTimestamp &&
          user.refreshToken == "refresh"
    })
    When("installing a success code for a new user")
    val googleUser = googleTokenService.userOf("success")
    Then("a new user will be created with the correct name and email address.")
    googleUser should equal(Some(GoogleUser("freddie.mercury@gmail.com", "Freddie Mercury")))
  }

  test("Install a success code for an existing user") {
    val (userDao, nowService, googleRequestService, googleTokenService) = createServices
    (userDao.findByEmailAddress _).expects("freddie.mercury@gmail.com").returning(
      Some(User("freddie.mercury@gmail.com", "access", "05/09/2014 10:12:00".toDate, "refresh")))
    (googleRequestService.sendTokenPostRequest _).expects(
      googleConstants.oauthTokenUrl,
      Map(
        "client_secret" -> googleConfiguration.consumerSecret,
        "client_id" -> googleConfiguration.consumerId,
        "grant_type" -> "authorization_code",
        "code" -> "success",
        "redirect_uri" -> googleConfiguration.callbackUrl)).returning(
        TokenResponse("access", Some(3600), None))
    (googleRequestService.sendProfileGetRequest _).expects(
      googleConstants.userProfileUrl.withParameters("access_token" -> "access")).returning(
        UserInfo("freddie.mercury@gmail.com", "Freddie Mercury"))
    When("installing a success code for an existing user")
    val googleUser = googleTokenService.userOf("success")
    Then("a new user will be created with the correct name and email address.")
    googleUser should equal(Some(GoogleUser("freddie.mercury@gmail.com", "Freddie Mercury")))
  }

  test("Refuse access to a non-authorisable user") {
    val (userDao, nowService, googleRequestService, googleTokenService) = createServices
    (googleRequestService.sendTokenPostRequest _).expects(
      googleConstants.oauthTokenUrl,
      Map(
        "client_secret" -> googleConfiguration.consumerSecret,
        "client_id" -> googleConfiguration.consumerId,
        "grant_type" -> "authorization_code",
        "code" -> "success",
        "redirect_uri" -> googleConfiguration.callbackUrl)).returning(TokenResponse("access", Some(3600), None))
    (googleRequestService.sendProfileGetRequest _).expects(
      googleConstants.userProfileUrl.withParameters("access_token" -> "access")).returning(
        UserInfo("john.deacon@gmail.com", "John Deacon"))
    googleTokenService.userOf("success") should equal(None)
  }

  test("Get a non-expired access token") {
    val (userDao, nowService, googleRequestService, googleTokenService) = createServices
    (nowService.now _).expects().returning("05/09/2014 09:12:00".toTime)
    val user = User("user", "access", "05/10/2014 15:00:00".toDate, "refresh")
    When("getting a user's non-expired access token")
    Then("the original access token should be returned")
    googleTokenService.accessToken(user) should equal("access")
  }

  test("Get a new access token as the user's current token has expired") {
    val (userDao, nowService, googleRequestService, googleTokenService) = createServices
    (nowService.now _).expects().returning("05/09/2014 09:12:00".toTime).anyNumberOfTimes
    (userDao.store _).expects(where {
      (user: User) =>
        user.username == "user" &&
          user.accessToken == "new access" &&
          user.expiryDate == "05/09/2014 10:12:00".toTimestamp
        user.refreshToken == "refresh"
    })
    val user = User("user", "access", "05/10/2013 15:00:00".toDate, "refresh")
    (googleRequestService.sendTokenPostRequest _).expects(
      googleConstants.oauthTokenUrl,
      Map(
        "client_secret" -> googleConfiguration.consumerSecret,
        "client_id" -> googleConfiguration.consumerId,
        "refresh_token" -> "refresh",
        "grant_type" -> "refresh_token")).returning(
        TokenResponse("new access", Some(3600), None))
    When("getting an access token as the current token has expired")
    Then("the access token should be the one returned from Google")
    googleTokenService.accessToken(user) should equal("new access")
    Then("the user should be updated to be associated with the new token")
    user.accessToken should equal("new access")
    user.expiryDate should equal("05/09/2014 10:12:00".toTimestamp)
  }

  def createServices = {
    val nowService = mock[NowService]
    val googleRequestService = mock[GoogleRequestService]
    val userDao = mock[UserDao]
    (
      userDao,
      nowService,
      googleRequestService,
      new GoogleTokenServiceImpl(
        userDao,
        googleConfiguration,
        googleConstants,
        nowService,
        googleRequestService,
        new StaticGoogleAuthorisationService(List("freddie.mercury@gmail.com", "brian.may@gmail.com"))))
  }

  implicit class DateFormatImplicits(date: String) {

    val df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    def toTime = date.toDate.getTime()
    def toTimestamp = new Timestamp(toTime)
    def toDate = df.parse(date)
  }

}