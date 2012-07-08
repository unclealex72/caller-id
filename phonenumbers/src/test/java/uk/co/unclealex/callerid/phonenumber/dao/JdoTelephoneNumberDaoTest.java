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

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;
import uk.co.unclealex.hbase.testing.DatanucleusContext;
import uk.co.unclealex.hbase.testing.HBaseTestContainer;
import uk.co.unclealex.hbase.testing.HBaseTestContainer.Port;

/**
 * @author alex
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { uk.co.unclealex.callerid.phonenumber.dao.JdoTelephoneNumberDaoTest.Context.class })
@Transactional
@TransactionConfiguration
public class JdoTelephoneNumberDaoTest {

  static HBaseTestContainer container;
  
  @BeforeClass
  public static void initialiseHBase() throws Exception {
    container = new HBaseTestContainer().withPort(Port.ZOOKEEPER_CLIENT, 2181).start();
  }

  @AfterClass
  public static void destroyHBase() throws Exception {
    container.stop();
  }

  @Autowired
  private TelephoneNumberDao telephoneNumberDao;

  @Autowired
  private PersistenceManagerFactory persistenceManagerFactory;

  @Before
  public void clear() throws IOException {
    getPersistenceManagerFactory().getPersistenceManager().newQuery(TelephoneNumber.class, "").deletePersistentAll();
  }

  @Test
  public void testStoreAndFind() {
    String expectedInternationalPrefix = "44";
    String expectedStdCode = "1783";
    String expectedNumber = "224466";
    TelephoneNumber telephoneNumber = new TelephoneNumber(expectedInternationalPrefix, expectedStdCode, expectedNumber);
    getTelephoneNumberDao().store(telephoneNumber);
    TelephoneNumber actualTelephoneNumber =
        getTelephoneNumberDao().findByNumber(expectedInternationalPrefix, expectedStdCode, expectedNumber);
    Assert.assertNotNull("Could not retrieve a stored telephone number.", actualTelephoneNumber);
    Assert.assertEquals(
        "The telephone number had the wrong international prefix.",
        expectedInternationalPrefix,
        actualTelephoneNumber.getInternationalPrefix());
    Assert.assertEquals(
        "The telephone number had the wrong std code.",
        expectedStdCode,
        actualTelephoneNumber.getStdCode());
    Assert
        .assertEquals("The telephone number had the wrong number.", expectedNumber, actualTelephoneNumber.getNumber());
  }

  @Test
  public void testStoreNonUnique() {
    TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
    telephoneNumberDao.store(new TelephoneNumber("1", "800", "5551122"));
    telephoneNumberDao.store(new TelephoneNumber("1", "808", "5551123"));
  }

  @Test
  public void testRemove() {
    TelephoneNumber telephoneNumber = new TelephoneNumber("44", "1208", "732908");
    TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
    telephoneNumberDao.store(telephoneNumber);
    telephoneNumberDao.remove(telephoneNumber);
    List<?> managedObjects =
        (List<?>) getPersistenceManagerFactory().getPersistenceManager().newQuery(TelephoneNumber.class, "").execute();
    Assert.assertEquals("The telephone number was not removed.", 0, managedObjects.size());
  }

  interface TelephoneNumberFinder {
    public TelephoneNumber findTelephoneNumber(TelephoneNumberDao telephoneNumberDao, String id);
  }

  @Configuration
  @ImportResource({"classpath:application-context-datanucleus-test.xml", "classpath:application-context-phonenumbers.xml"})
  public static class Context extends DatanucleusContext {
    
    public HBaseTestContainer getContainer() {
      return container;
    }
  }

  public TelephoneNumberDao getTelephoneNumberDao() {
    return telephoneNumberDao;
  }

  public PersistenceManagerFactory getPersistenceManagerFactory() {
    return persistenceManagerFactory;
  }

}
