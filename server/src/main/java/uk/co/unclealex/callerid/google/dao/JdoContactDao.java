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

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.google.model.QContact;
import uk.co.unclealex.persistence.jdo.JdoBasicDao;
import uk.co.unclealex.persistence.paging.PagingService;

/**
 * @author alex
 * 
 */
@Transactional
public class JdoContactDao extends JdoBasicDao<Contact, QContact> implements ContactDao {

  /**
   * @param persistenceManagerFactory
   * @param pagingService
   */
  public JdoContactDao(PersistenceManagerFactory persistenceManagerFactory, PagingService pagingService) {
    super(Contact.class, persistenceManagerFactory, pagingService);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<Contact> findByTelephoneNumber(final String telephoneNumber) {
    return query().filter(candidate().telephoneNumbers.contains(telephoneNumber)).executeList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Contact findByName(final String name) {
    return query().filter(candidate().name.eq(name)).executeUnique();
  }

  @Override
  public QContact candidate() {
    return QContact.candidate();
  }
}
