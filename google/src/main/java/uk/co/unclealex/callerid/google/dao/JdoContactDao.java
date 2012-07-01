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

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

/**
 * @author alex
 * 
 */
public class JdoContactDao implements ContactDao {

  private final PersistenceManagerFactory persistenceManagerFactory;

  public JdoContactDao(PersistenceManagerFactory persistenceManagerFactory) {
    super();
    this.persistenceManagerFactory = persistenceManagerFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void store(Contact contact) {
    getPersistenceManager().makePersistent(contact);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<Contact> findByTelephoneNumber(TelephoneNumber telephoneNumber) {
    Query query = getPersistenceManager().newQuery(Contact.class);
    query.declareImports("import " + TelephoneNumber.class.getName());
    query.setFilter("telephoneNumbers.contains(telephoneNumber) "
        + "&& telephoneNumber.internationalPrefix == internationalPrefix "
        + "&& telephoneNumber.stdCode == stdCode "
        + "&& telephoneNumber.number == number");
    query.declareVariables("TelephoneNumber telephoneNumber");
    query.declareParameters("String internationalPrefix, "
        + "String stdCode, "
        + "String number");
    return (List<Contact>) query.execute(
        telephoneNumber.getInternationalPrefix(),
        telephoneNumber.getStdCode(),
        telephoneNumber.getNumber());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Contact findByName(String name) {
    Query query = getPersistenceManager().newQuery(Contact.class);
    query.setFilter("name == name");
    query.declareVariables("String name");
    @SuppressWarnings("unchecked")
    List<Contact> contacts = (List<Contact>) query.execute(name);
    return contacts.isEmpty() ? null : contacts.get(0);
  }

  public PersistenceManager getPersistenceManager() {
    return getPersistenceManagerFactory().getPersistenceManager();
  }

  public PersistenceManagerFactory getPersistenceManagerFactory() {
    return persistenceManagerFactory;
  }

}
