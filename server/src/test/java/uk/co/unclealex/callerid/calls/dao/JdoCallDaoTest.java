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

  @Autowired
  CallDao callDao;
  @Autowired
  PersistenceManagerFactory persistenceManagerFactory;

  @Test
  public void testCreate() {
    DateTime now = new DateTime();
    Call call = new Call(now, "441256999888", "Brian");
    callDao.store(call);
    flush();
    @SuppressWarnings("unchecked")
    List<Call> actualCalls = (List<Call>) persistenceManager().newQuery(Call.class).execute();
    Assert.assertEquals("The wrong number of calls were returned.", 1, actualCalls.size());
    Call actualCall = actualCalls.get(0);
    Assert.assertEquals("The call had the wrong time.", now, actualCall.getCallTime());
    Assert.assertEquals("The call had the wrong telephone number.", "441256999888", actualCall.getTelephoneNumber());
    Assert.assertEquals("The call had the wrong contact name.", "Brian", actualCall.getContactName());
  }

  @Test
  public void testUpdate() {
    DateTime now = new DateTime();
    Call call = new Call(now, "441256999888", "Brian");
    callDao.store(call);
    flush();
    callDao.updateContactName(call.getId(), "Freddie");
    flush();
    @SuppressWarnings("unchecked")
    List<Call> actualCalls = (List<Call>) persistenceManager().newQuery(Call.class).execute();
    Assert.assertEquals("The wrong number of calls were returned.", 1, actualCalls.size());
    Call actualCall = actualCalls.get(0);
    Assert.assertEquals("The call had the wrong time.", now, actualCall.getCallTime());
    Assert.assertEquals("The call had the wrong telephone number.", "441256999888", actualCall.getTelephoneNumber());
    Assert.assertEquals("The call had the wrong contact name.", "Freddie", actualCall.getContactName());
  }

  @Test
  public void testPageByDate() {
    DateTime firstCallTime = new DateTime();
    DateTime secondCallTime = firstCallTime.plusHours(1);
    DateTime thirdCallTime = secondCallTime.plusHours(2);
    callDao.store(new Call(secondCallTime, "44800400100", "Freddie"));
    callDao.store(new Call(firstCallTime, "33900505050", "Brian"));
    callDao.store(new Call(thirdCallTime, "33900505050", "Brian"));
    flush();
    List<List<DateTime>> expectedDateTimes = Lists.newArrayList();
    expectedDateTimes.add(Arrays.asList(firstCallTime, secondCallTime));
    expectedDateTimes.add(Arrays.asList(thirdCallTime));
    long currentPage = 1;
    for (List<DateTime> expectedPageDateTimes : expectedDateTimes) {
      Page<Call> page = callDao.pageAllByTimeReceived(currentPage, 2);
      Assert.assertEquals("The wrong number of pages were returned.", expectedDateTimes.size(), page
          .getPageOffsetsByPageNumber()
          .size());
      List<DateTime> actualDateTimes = project(page.getElements(), DateTime.class, on(Call.class).getCallTime());
      Assert.assertEquals("The wrong times were returned.", actualDateTimes, expectedPageDateTimes);
      currentPage++;
    }
  }

  @Test
  public void testMostRecentCallForNumberWithNoPreviousCall() {
    callDao.store(new Call(new DateTime(), "44800400100", "Mike"));
    flush();
    Assert.assertNull(
        "A contact name was returned for a call that has never been made.",
        callDao.getMostRecentContactNameForPhoneNumber("44800500500"));
  }

  @Test
  public void testMostRecentCallForNumberWithPreviousCalls() {
    callDao.store(new Call(new DateTime(), "44800400100", "Brian"));
    callDao.store(new Call(new DateTime().minusDays(1), "44800500500", "Mike"));
    callDao.store(new Call(new DateTime().minusDays(2), "44800500500", null));
    callDao.store(new Call(new DateTime().minusDays(3), "44800500500", "Ian"));
    flush();
    Assert.assertEquals(
        "The wrong name was recevied for a most recent call.",
        "Mike",
        callDao.getMostRecentContactNameForPhoneNumber("44800500500"));
  }

  @Test
  public void testMostRecentCallForNumberWithPreviousCallsAndNullOverrides() {
    callDao.store(new Call(new DateTime(), "44800400100", "Brian"));
    callDao.store(new Call(new DateTime().minusDays(1), "44800500500", null));
    callDao.store(new Call(new DateTime().minusDays(2), "44800500500", "Mike"));
    callDao.store(new Call(new DateTime().minusDays(3), "44800500500", "Ian"));
    flush();
    Assert.assertNull(
        "A name was returned for a call where null was the most recent name.",
        callDao.getMostRecentContactNameForPhoneNumber("44800500500"));
  }

  protected void flush() {
    persistenceManager().flush();
  }

  protected PersistenceManager persistenceManager() {
    return persistenceManagerFactory.getPersistenceManager();
  }

}
