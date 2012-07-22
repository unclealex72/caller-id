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

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

/**
 * @author alex
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application-context-callerid-google-test.xml")
@Transactional
@TransactionConfiguration
public class JdoContactDaoTest {

  @Autowired
  private ContactDao contactDao;

  @Autowired
  private PersistenceManagerFactory persistenceManagerFactory;

  @SuppressWarnings("unchecked")
  @Before
  public void clear() {
    PersistenceManager persistenceManager = getPersistenceManagerFactory().getPersistenceManager();
    persistenceManager.newQuery(Contact.class, "").deletePersistentAll();
    Assert.assertFalse(
        "Not all object references were removed.",
        ((Iterable<Object>) persistenceManager.newQuery(Contact.class, "").execute()).iterator().hasNext());
  }

  @Test
  public void testFindByTelephoneNumber() {
    TelephoneNumber numberOne = telephoneNumber("44", "800", "118118");
    TelephoneNumber numberTwo = telephoneNumber("1", "214", "555976");
    TelephoneNumber numberThree = telephoneNumber("1", "714", "888888");
    TelephoneNumber numberFour = telephoneNumber("33", "999", "000000");
    createContact("tom", numberOne, numberTwo);
    createContact("dick", numberThree, numberOne);
    createContact("harry", numberFour);
    flush();
    List<Contact> contacts = getContactDao().findByTelephoneNumber(numberOne);
    Assert.assertThat("The wrong contacts were returned for a telephone number.", 
        project(contacts, String.class, on(Contact.class).getName()),
        Matchers.containsInAnyOrder("tom", "dick"));
  }

  protected TelephoneNumber telephoneNumber(String internationalPrefix, String stdCode, String number) {
    return new TelephoneNumber(internationalPrefix, stdCode, number);
  }

  protected Contact createContact(String name, TelephoneNumber... telephoneNumbers) {
    Contact contact = Contact.create(name, telephoneNumbers);
    PersistenceManager persistenceManager = getPersistenceManagerFactory().getPersistenceManager();
    persistenceManager.makePersistent(contact);
    return contact;
  }

  public ContactDao getContactDao() {
    return contactDao;
  }

  public void setContactDao(ContactDao contactDao) {
    this.contactDao = contactDao;
  }

  public void flush() {
    persistenceManager().flush();
  }
  
  public PersistenceManager persistenceManager() {
    return getPersistenceManagerFactory().getPersistenceManager();
  }
  
  public PersistenceManagerFactory getPersistenceManagerFactory() {
    return persistenceManagerFactory;
  }

  public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
    this.persistenceManagerFactory = persistenceManagerFactory;
  }

}
