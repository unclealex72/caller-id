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

package uk.co.unclealex.callerid.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.unclealex.callerid.calls.dao.CallDao;
import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.google.dao.ContactDao;
import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

import com.google.common.base.Function;

/**
 * @author alex
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ReceivedCallFactoryImplTest {

  @Mock
  PhoneNumberFactory phoneNumberFactory;
  @Mock
  Function<PhoneNumber, String> phoneNumberNormaliser;
  @Mock
  CallDao callDao;
  @Mock
  ContactDao contactDao;
  ReceivedCallFactory receivedCallFactory;

  @Before
  public void setup() {
    receivedCallFactory = new ReceivedCallFactoryImpl(phoneNumberFactory, callDao, contactDao, phoneNumberNormaliser);
    when(phoneNumberFactory.create("444555")).thenReturn(new NumberOnlyPhoneNumber("444555"));
    when(phoneNumberFactory.create("441256444555")).thenReturn(new NumberOnlyPhoneNumber("441256444555"));
    when(phoneNumberNormaliser.apply(new NumberOnlyPhoneNumber("444555"))).thenReturn("441256444555");
    when(callDao.getMostRecentContactNameForPhoneNumber("441256444555")).thenReturn("Iain");
    when(contactDao.findByTelephoneNumber("441256444555")).thenReturn(new ArrayList<Contact>());
  }

  @Test
  public void testCreateCallFromStoredCall() {
    DateTime callTime = new DateTime();
    Call call = new Call(callTime, "441256444555", "Brian");
    Assert.assertEquals("The wrong received call was returned.", new ReceivedCall(callTime, new NumberOnlyPhoneNumber(
        "441256444555"), "Brian", new ArrayList<Contact>()), receivedCallFactory.create(call));
  }

  @Test
  public void testCreateCallFromCurrentCall() {
    DateTime callTime = new DateTime();
    Assert.assertEquals("The wrong received call was returned.", new ReceivedCall(callTime, new NumberOnlyPhoneNumber(
        "444555"), "Iain", new ArrayList<Contact>()), receivedCallFactory.create(callTime, "444555"));
  }

}
