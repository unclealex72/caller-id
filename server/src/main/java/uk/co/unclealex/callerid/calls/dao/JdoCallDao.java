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

import javax.jdo.PersistenceManagerFactory;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jdo.JDOQLQuery;

import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.QCall;
import uk.co.unclealex.persistence.jdo.JdoBasicDao;
import uk.co.unclealex.persistence.paging.Page;
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
    super(persistenceManagerFactory, pagingService);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<Call> pageAllByTimeReceived(long pageNumber, long pageSize) {
    final QCall call = candidate();
    PageQueryCallback pageQueryCallback = new PageQueryCallback() {
      @Override
      public JDOQLQuery doInQuery(JDOQLQuery query) {
        return query.from(call).orderBy(call.callTime.asc());
      }
    };
    return page(pageQueryCallback, call, pageNumber, pageSize);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateContactName(final int callId, final String newContactName) {
    UniqueQueryCallback callback = new UniqueQueryCallback() {
      @Override
      public Call doInQuery(JDOQLQuery query) {
        QCall c = candidate();
        Call call = query.from(c).where(c.id.eq(callId)).uniqueResult(c);
        call.setContactName(newContactName);
        return call;
      }
    };
    execute(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMostRecentContactNameForPhoneNumber(final String phoneNumber) {
    QueryCallback<String> callback = new QueryCallback<String>() {

      @Override
      public String doInQuery(JDOQLQuery query) {
        QCall c = candidate();
        return query
            .from(c)
            .where(c.telephoneNumber.eq(phoneNumber))
            .orderBy(c.callTime.desc())
            .limit(1)
            .uniqueResult(c.contactName);
      }
    };
    return execute(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QCall candidate() {
    return QCall.call;
  }

}
