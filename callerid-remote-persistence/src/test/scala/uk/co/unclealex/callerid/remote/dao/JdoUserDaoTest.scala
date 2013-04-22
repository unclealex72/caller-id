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
import javax.jdo.PersistenceManager
import javax.jdo.PersistenceManagerFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.co.unclealex.callerid.remote.model.OauthToken
import uk.co.unclealex.callerid.remote.model.OauthTokenType
import uk.co.unclealex.callerid.remote.model.User

import org.junit.Assert._
import org.junit.Test
import scala.collection.JavaConversions._
import org.hamcrest.Matchers._
/**
 * @author alex
 *
 */
class JdoUserDaoTest extends AbstractDaoTest {

  @Autowired var userDao: UserDao = null

  @Autowired var pmf: PersistenceManagerFactory = null

  val df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

  @Test
  def testStoreAndUpdate: Unit = {
    val refreshToken = new OauthToken() init { t =>
      t.setToken("refresh")
      t.setTokenType(OauthTokenType.REFRESH)
    }
    val accessToken = new OauthToken() init { t =>
      t.setToken("access")
      t.setTokenType(OauthTokenType.ACCESS)
      t.setExpiryDate(df.parse("05/09/1972 09:12:00"))
    }
    val user = new User() init { u =>
      u.setUsername("alex")
      u.setOauthTokens(List(refreshToken))
    }
    userDao.store(user)
    persistenceManager.flush
    user.getOauthTokens += accessToken
    userDao.store(user)
    val persistedUser = userDao.getAll()(0)
    assertThat("The persisted user had the wrong tokens.", persistedUser.getOauthTokens,
      containsInAnyOrder(refreshToken, accessToken))
    persistedUser.getOauthTokens.find { _.getTokenType == OauthTokenType.ACCESS }.get.setToken("accessed")
    userDao.store(persistedUser)
    assertEquals("The access token was not updated correctly.", "accessed",
      userDao.getAll()(0).getOauthTokens.find { _.getTokenType == OauthTokenType.ACCESS }.get.getToken)
  }

  def persistenceManager = pmf.getPersistenceManager()
}
