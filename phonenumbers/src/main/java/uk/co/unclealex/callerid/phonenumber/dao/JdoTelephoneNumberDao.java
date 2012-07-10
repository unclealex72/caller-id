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
 * @author unclealex72
 *
 */

package uk.co.unclealex.callerid.phonenumber.dao;

import javax.jdo.PersistenceManagerFactory;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jdo.JDOQLQuery;

import uk.co.unclealex.callerid.persistence.JdoBasicDao;
import uk.co.unclealex.callerid.phonenumber.model.QTelephoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

/**
 * @author alex
 * 
 */
@Transactional
public class JdoTelephoneNumberDao extends JdoBasicDao<TelephoneNumber, QTelephoneNumber> implements TelephoneNumberDao {

  public JdoTelephoneNumberDao(PersistenceManagerFactory persistenceManagerFactory) {
    super(persistenceManagerFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TelephoneNumber findByNumber(final String internationalPrefix, final String stdCode, final String number) {
    UniqueQueryCallback callback = new UniqueQueryCallback() {
      public TelephoneNumber doInQuery(JDOQLQuery query) {
        QTelephoneNumber telephoneNumber = QTelephoneNumber.telephoneNumber;
        return query
            .from(telephoneNumber)
            .where(
                telephoneNumber.internationalPrefix.eq(internationalPrefix),
                telephoneNumber.stdCode.eq(stdCode),
                telephoneNumber.number.eq(number))
            .uniqueResult(telephoneNumber);

      }
    };
    return execute(callback);
  }

  @Override
  public QTelephoneNumber candidate() {
    return QTelephoneNumber.telephoneNumber;
  }
}
