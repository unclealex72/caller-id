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

package uk.co.unclealex.callerid.remote.dao;

import javax.inject.Inject;
import javax.jdo.PersistenceManagerFactory;

import uk.co.unclealex.callerid.remote.model.QUser;
import uk.co.unclealex.callerid.remote.model.User;
import uk.co.unclealex.persistence.jdo.JdoBasicDao;
import uk.co.unclealex.persistence.paging.PagingService;

/**
 * The JDO implementation of {@link UserDao}
 * 
 * @author alex
 * 
 */
public class JdoUserDao extends JdoBasicDao<User, QUser> implements UserDao {

  /**
   * @param persistenceManagerFactory
   * @param pagingService
   */
  @Inject
  public JdoUserDao(final PersistenceManagerFactory persistenceManagerFactory, final PagingService pagingService) {
    super(User.class, persistenceManagerFactory, pagingService);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QUser candidate() {
    return QUser.candidate();
  }
}