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

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

/**
 * @author alex
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
		"classpath:application-context-phonenumbers.xml",
		"classpath:application-context-phonenumbers-test.xml" })
@Transactional
@TransactionConfiguration
public class JdoTelephoneNumberDaoTest {

	@Autowired
	private TelephoneNumberDao telephoneNumberDao;

	@Autowired
	private PersistenceManagerFactory persistenceManagerFactory;

	@Before
	public void clear() {
		getPersistenceManagerFactory().getPersistenceManager().newQuery(TelephoneNumber.class, "").deletePersistentAll();
	}

	@Test
	public void testStoreAndFindByValues() {
		TelephoneNumberFinder telephoneNumberFinder = new TelephoneNumberFinder() {

			@Override
			public TelephoneNumber findTelephoneNumber(TelephoneNumberDao telephoneNumberDao, String id) {
				return telephoneNumberDao.findByNumber("33", "114", "225566");
			}
		};
		testStoreAndFind("33", "114", "225566", telephoneNumberFinder);
	}

	@Test
	public void testStoreAndFindById() {
		TelephoneNumberFinder telephoneNumberFinder = new TelephoneNumberFinder() {

			@Override
			public TelephoneNumber findTelephoneNumber(TelephoneNumberDao telephoneNumberDao, String id) {
				return telephoneNumberDao.findById(id);
			}
		};
		testStoreAndFind("44", "1783", "224466", telephoneNumberFinder);
	}

	protected void testStoreAndFind(
			String expectedInternationalPrefix,
			String expectedStdCode,
			String expectedNumber,
			TelephoneNumberFinder telephoneNumberFinder) {
		TelephoneNumber telephoneNumber = new TelephoneNumber(expectedInternationalPrefix, expectedStdCode, expectedNumber);
		getTelephoneNumberDao().store(telephoneNumber);
		TelephoneNumber actualTelephoneNumber =
				telephoneNumberFinder.findTelephoneNumber(getTelephoneNumberDao(), telephoneNumber.getId());
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

	@Test(expected = JDOUserException.class)
	public void testStoreUnique() {
		TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
		for (int idx = 0; idx < 2; idx++) {
			telephoneNumberDao.store(new TelephoneNumber("1", "800", "5551122"));
			Transaction tx = getPersistenceManagerFactory().getPersistenceManager().currentTransaction();
			tx.commit();
			tx.begin();
		}
	}

	@Test
	public void testRemove() {
		TelephoneNumber telephoneNumber = new TelephoneNumber("44", "1208", "732908");
		TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
		telephoneNumberDao.store(telephoneNumber);
		String id = telephoneNumber.getId();
		telephoneNumberDao.remove(id);
		getPersistenceManagerFactory().getPersistenceManager().currentTransaction().commit();
		Assert.assertNull("The telephone number was not removed.", telephoneNumberDao.findById(id));
	}

	interface TelephoneNumberFinder {
		public TelephoneNumber findTelephoneNumber(TelephoneNumberDao telephoneNumberDao, String id);
	}

	public TelephoneNumberDao getTelephoneNumberDao() {
		return telephoneNumberDao;
	}

	public PersistenceManagerFactory getPersistenceManagerFactory() {
		return persistenceManagerFactory;
	}

}
