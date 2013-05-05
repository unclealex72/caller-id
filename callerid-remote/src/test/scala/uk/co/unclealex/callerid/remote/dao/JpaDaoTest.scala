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

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.GivenWhenThen
import javax.persistence.Persistence
import javax.persistence.EntityManager
import org.scalatest.BeforeAndAfterEach

/**
 * @author alex
 *
 */
class JpaDaoTest extends FunSuite with ShouldMatchers with GivenWhenThen with BeforeAndAfterEach {

  var em: EntityManager = _

  override def beforeEach {
    val emf = Persistence.createEntityManagerFactory("calls")
    em = emf createEntityManager
  }

  override def afterEach {
    em close
  }
}