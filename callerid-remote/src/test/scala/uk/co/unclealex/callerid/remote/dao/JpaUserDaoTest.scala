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
package uk.co.unclealex.callerid.remote.dao

import java.text.DateFormat
import java.text.SimpleDateFormat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.co.unclealex.callerid.remote.model.OauthToken
import uk.co.unclealex.callerid.remote.model.OauthTokenType
import uk.co.unclealex.callerid.remote.model.User
import org.junit.Assert._
import org.junit.Test
import scala.collection.JavaConversions._
import org.hamcrest.Matchers._
import java.util.ArrayList
import scala.collection.mutable.ListBuffer
import javax.persistence.EntityManagerFactory
/**
 * @author alex
 *
 */
class JpaUserDaoTest extends JpaDaoTest {

  /*
  val df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

  test("store and update users") {
    val userDao = new JpaUserDao(em)
    val refreshToken = new OauthToken(token = "refresh",
      tokenType = OauthTokenType.REFRESH)
    val accessToken = new OauthToken(
      token = "access",
      tokenType = OauthTokenType.ACCESS,
      expiryDate = df.parse("05/09/1972 09:12:00"))
    val user = new User("alex")
    user.oauthTokens += refreshToken
    userDao.store(user)
    user.oauthTokens += accessToken
    userDao.store(user)
    When("storing tokens for a user")
    val persistedUser = userDao.getAll(0)
    Then("the returned tokens should be the same as those stored.")
    persistedUser.oauthTokens.toSet should equal(Set(refreshToken, accessToken))
    When("updating a user's token")
    persistedUser.oauthTokens.find { _.tokenType == OauthTokenType.ACCESS }.get.token = "accessed"
    userDao.store(persistedUser)
    Then("it should be associated with the persisted user")
    userDao.getAll(0).oauthTokens.find { _.tokenType == OauthTokenType.ACCESS }.get.token should equal("accessed")
  }
  */
}
