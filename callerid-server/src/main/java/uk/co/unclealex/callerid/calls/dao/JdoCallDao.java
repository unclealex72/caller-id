/**
 * Copyright 2012 Alex Jones
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

package uk.co.unclealex.callerid.calls.dao;

import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.query.typesafe.TypesafeQuery;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.QCall;
import uk.co.unclealex.persistence.jdo.JdoBasicDao;
import uk.co.unclealex.persistence.paging.PagingService;

/**
 * The JDO implementation of {@link CallDao}.
 * 
 * @author alex
 * 
 */
@Transactional
public class JdoCallDao extends JdoBasicDao<Call, QCall> implements CallDao {

  /**
   * @param persistenceManagerFactory
   * @param pagingService
   */
  public JdoCallDao(PersistenceManagerFactory persistenceManagerFactory, PagingService pagingService) {
    super(Call.class, persistenceManagerFactory, pagingService);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Call> getAllByTimeReceived() {
    TypesafeQuery<Call> query = query().orderBy(candidate().callTime.desc());
    List<Call> results = query.executeList();
    return fetch(results);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void updateContactName(final int callId, final String newContactName) {
    Call call = query().filter(candidate().id.eq(callId)).executeUnique();
    call.setContactName(newContactName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMostRecentContactNameForPhoneNumber(final String phoneNumber) {
    QCall c = candidate();
    return query()
        .filter(c.telephoneNumber.eq(phoneNumber))
        .orderBy(c.callTime.desc())
        .range(0, 1)
        .executeResultUnique(String.class, true, c.contactName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QCall candidate() {
    return QCall.candidate();
  }

}
