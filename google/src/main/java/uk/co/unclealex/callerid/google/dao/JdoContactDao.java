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

package uk.co.unclealex.callerid.google.dao;

import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.google.model.QContact;
import uk.co.unclealex.callerid.persistence.JdoBasicDao;
import uk.co.unclealex.callerid.phonenumber.model.QTelephoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

import com.mysema.query.jdo.JDOQLQuery;

/**
 * @author alex
 * 
 */
public class JdoContactDao extends JdoBasicDao<Contact, QContact> implements ContactDao {

  public JdoContactDao(PersistenceManagerFactory persistenceManagerFactory) {
    super(persistenceManagerFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Contact> findByTelephoneNumber(final TelephoneNumber telephoneNumber) {
    ListQueryCallback callback = new ListQueryCallback() {
      @Override
      public List<Contact> doInQuery(JDOQLQuery query) {
        QContact contact = QContact.contact;
        QTelephoneNumber tn = QTelephoneNumber.telephoneNumber;
        return query
            .from(contact, tn)
            .where(
                contact.telephoneNumbers.contains(tn),
                tn.internationalPrefix.eq(telephoneNumber.getInternationalPrefix()),
                tn.stdCode.eq(telephoneNumber.getStdCode()),
                tn.number.eq(telephoneNumber.getNumber()))
            .list(contact);
      }
    };
    return execute(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Contact findByName(final String name) {
    UniqueQueryCallback callback = new UniqueQueryCallback() {
      @Override
      public Contact doInQuery(JDOQLQuery query) {
        QContact contact = QContact.contact;
        return query.from(contact).where(contact.name.eq(name)).uniqueResult(contact);
      }
    };
    return execute(callback);
  }

  @Override
  public QContact candidate() {
    return QContact.contact;
  }
}
