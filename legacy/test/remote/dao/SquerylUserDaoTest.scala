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
package legacy.remote.dao

import java.sql.Timestamp
import java.text.SimpleDateFormat

import legacy.remote.model.User
/**
 * @author alex
 *
 */
class SquerylUserDaoTest extends SquerylDaoTest {

  test("store and update users") {
    val userDao = new SquerylUserDao
    val user = User("alex", "access", "05/09/1972 09:12:00".toDate, "refresh")
    userDao store user
    user.accessToken = "accessed"
    user.expiryDate = "05/09/1972 10:12:00".toDate
    user.refreshToken = "refreshed"
    userDao store user
    When("updating a single user")
    Then("there should only be one user")
    val users = userDao.getAll
    users should have size (1)
    val persistedUser = users(0)
    Then("the access token should be the updated access token")
    persistedUser.accessToken should equal("accessed")
    Then("the expiry date should be the updated expiry date")
    persistedUser.expiryDate should equal("05/09/1972 10:12:00".toDate)
    Then("the refresh token should be the updated refresh token")
    persistedUser.refreshToken should equal("refreshed")
  }

  test("Find valid user by email address") {
    val userDao = new SquerylUserDao
    userDao store User("alex", "access", "05/09/1972 09:12:00".toDate, "refresh")
    When("finding an existing user")
    val persistedUser = userDao.findByEmailAddress("alex")
    Then("they should first be found")
    persistedUser should not equal (None)
    persistedUser.map { user =>
      Then("they should have the correct username")
      user.username should equal("alex")
      Then("they should have the correct access token")
      user.accessToken should equal("access")
      Then("they should have the correct expiry date")
      user.expiryDate should equal("05/09/1972 09:12:00".toDate)
      Then("they should have the correct refresh token")
      user.refreshToken should equal("refresh")
    }
  }

  test("Fail to find invalid user by email address") {
    val userDao = new SquerylUserDao
    userDao store User("brian", "access", "05/09/1972 09:12:00".toDate, "refresh")
    When("looking for a non-existing user")
    val persistedUser = userDao.findByEmailAddress("alex")
    Then("they should not be found")
    persistedUser should equal(None)
  }

  implicit class StringImplicits(str: String) {

    val df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    def toDate = new Timestamp(df.parse(str).getTime())
  }
}
