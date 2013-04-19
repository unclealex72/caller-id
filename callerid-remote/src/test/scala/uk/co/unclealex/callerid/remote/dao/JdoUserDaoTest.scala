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

import static org.hamcrest.Matchers.*
import static org.junit.Assert.*

import static extension uk.co.unclealex.xtend.OptionalExtensions.*

/**
 * @author alex
 *
 */
class JdoUserDaoTest extends AbstractDaoTest {

    @Autowired
    extension var UserDao userDao

    @Autowired
    var PersistenceManagerFactory pmf

    extension val DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Test
    def void testStoreAndUpdate() {
        val refreshToken = new OauthToken => [
            token = "refresh"
            tokenType = OauthTokenType::REFRESH
        ]
        val accessToken = new OauthToken => [
            token = "access"
            tokenType = OauthTokenType::ACCESS
            expiryDate = "05/09/1972 09:12:00".parse.required
        ]
        val user = new User => [
            username = "alex"
            oauthTokens = newArrayList(refreshToken)
        ]
        user.store
        persistenceManager.flush
        user.oauthTokens += accessToken
        user.store
        val persistedUser = userDao.all.get(0)
        assertThat("The persisted user had the wrong tokens.", persistedUser.oauthTokens,
            containsInAnyOrder(refreshToken, accessToken))
        persistedUser.oauthTokens.findFirst[tokenType == OauthTokenType::ACCESS].token = "accessed"
        persistedUser.store
        assertEquals("The access token was not updated correctly.", "accessed",
            userDao.all.get(0).oauthTokens.findFirst[tokenType == OauthTokenType::ACCESS].token)
    }

    def PersistenceManager persistenceManager() {
        pmf.persistenceManager
    }
}
