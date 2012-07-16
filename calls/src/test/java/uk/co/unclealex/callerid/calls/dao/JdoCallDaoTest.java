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

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

import java.util.Arrays;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;
import uk.co.unclealex.persistence.paging.Page;

import com.google.common.collect.Lists;

/**
 * @author alex
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application-context-callerid-calls-test.xml")
@Transactional
@TransactionConfiguration
public class JdoCallDaoTest {

  @Autowired CallDao callDao;
  @Autowired PersistenceManagerFactory persistenceManagerFactory;
  
  @Test
  public void testCreate() {
    DateTime now = new DateTime();
    Call call = new Call(now, new TelephoneNumber("44", "1256", "999888"), "Brian");
    callDao.store(call);
    flush();
    @SuppressWarnings("unchecked")
    List<Call> actualCalls = (List<Call>) persistenceManager().newQuery(Call.class).execute();
    Assert.assertEquals("The wrong number of calls were returned.", 1, actualCalls.size());
    Call actualCall = actualCalls.get(0);
    Assert.assertEquals("The call had the wrong time.", now,  actualCall.getCallTime());
    TelephoneNumber actualTelephoneNumber = actualCall.getTelephoneNumber();
    Assert.assertNotNull("The call's telephone number was null.", actualTelephoneNumber);
    Assert.assertEquals("The call had the wrong international prefix.", "44",  actualTelephoneNumber.getInternationalPrefix());
    Assert.assertEquals("The call had the wrong STD code.", "1256",  actualTelephoneNumber.getStdCode());
    Assert.assertEquals("The call had the wrong number.", "999888",  actualTelephoneNumber.getNumber());
    Assert.assertEquals("The call had the wrong contact name.", "Brian", actualCall.getContactName());
  }

  @Test
  public void testUpdate() {
    DateTime now = new DateTime();
    Call call = call(now, "44", "1256", "999888", "Brian");
    callDao.store(call);
    flush();
    callDao.updateContactName(call.getId(), "Freddie");
    flush();
    @SuppressWarnings("unchecked")
    List<Call> actualCalls = (List<Call>) persistenceManager().newQuery(Call.class).execute();
    Assert.assertEquals("The wrong number of calls were returned.", 1, actualCalls.size());
    Call actualCall = actualCalls.get(0);
    Assert.assertEquals("The call had the wrong time.", now,  actualCall.getCallTime());
    TelephoneNumber actualTelephoneNumber = actualCall.getTelephoneNumber();
    Assert.assertNotNull("The call's telephone number was null.", actualTelephoneNumber);
    Assert.assertEquals("The call had the wrong international prefix.", "44",  actualTelephoneNumber.getInternationalPrefix());
    Assert.assertEquals("The call had the wrong STD code.", "1256",  actualTelephoneNumber.getStdCode());
    Assert.assertEquals("The call had the wrong number.", "999888",  actualTelephoneNumber.getNumber());
    Assert.assertEquals("The call had the wrong contact name.", "Freddie", actualCall.getContactName());
  }
  
  @Test
  public void testPageByDate() {
    DateTime firstCallTime = new DateTime();
    DateTime secondCallTime = firstCallTime.plusHours(1);
    DateTime thirdCallTime = secondCallTime.plusHours(2);
    callDao.store(call(secondCallTime, "44", "0800", "400100", "Freddie"));
    callDao.store(call(firstCallTime, "33", "0900", "505050", "Brian"));
    callDao.store(call(thirdCallTime, "33", "0900", "505050", "Brian"));
    flush();
    List<List<DateTime>> expectedDateTimes = Lists.newArrayList();
    expectedDateTimes.add(Arrays.asList(firstCallTime, secondCallTime));
    expectedDateTimes.add(Arrays.asList(thirdCallTime));
    long currentPage = 1;
    for (List<DateTime> expectedPageDateTimes : expectedDateTimes) {
      Page<Call> page = callDao.pageAllByTimeReceived(currentPage, 2);
      Assert.assertEquals("The wrong number of pages were returned.", expectedDateTimes.size(), page.getPageOffsetsByPageNumber().size());
      List<DateTime> actualDateTimes = project(page.getElements(), DateTime.class, on(Call.class).getCallTime());
      Assert.assertEquals("The wrong times were returned.", actualDateTimes, expectedPageDateTimes);
      currentPage++;
    }
  }
  
  public Call call(DateTime callTime, String internationalPrefix, String stdCode, String number, String contactName) {
    return new Call(callTime, new TelephoneNumber(internationalPrefix, stdCode, number), contactName);
  }
  protected void flush() {
    persistenceManager().flush();
  }

  protected PersistenceManager persistenceManager() {
    return persistenceManagerFactory.getPersistenceManager();
  }
  
}
