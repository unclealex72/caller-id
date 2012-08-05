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

package uk.co.unclealex.callerid.squeezebox;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;
import uk.co.unclealex.callerid.areacode.model.CountryCode;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberLocationVisitor;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberPrettyPrinterVisitor;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberWithheldVisitor;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;

import com.google.common.collect.Ordering;
import com.mycila.inject.internal.guava.collect.Lists;
import com.mycila.inject.internal.guava.collect.Sets;

/**
 * @author alex
 * 
 */
public class SqueezeboxCallListenerTest {

  SqueezeboxCallListener squeezeboxCallListener;
  SqueezeboxCli squeezeboxCli;

  @Before
  public void setup() {
    squeezeboxCli = mock(SqueezeboxCli.class);
    SqueezeboxCliFactory squeezeboxCliFactory = new SqueezeboxCliFactory() {
      @Override
      public SqueezeboxCli create() throws IOException {
        return squeezeboxCli;
      }
    };
    DefaultsService defaultsService = new DefaultsService() {
      @Override
      public String getInternationalPrefix() {
        return "00";
      }

      @Override
      public String getCountryCode() {
        return "44";
      }

      @Override
      public String getAreaCodePrefix() {
        return "0";
      }

      @Override
      public String getAreaCode() {
        return "1256";
      }
    };
    squeezeboxCallListener =
        new SqueezeboxCallListener(
            squeezeboxCliFactory,
            new PhoneNumberPrettyPrinterVisitor(defaultsService),
            new PhoneNumberLocationVisitor(),
            new PhoneNumberWithheldVisitor());
  }

  @Test
  public void testOnNumberWithheld() {
    ReceivedCall receivedCall = new ReceivedCall(new Date(), new WithheldPhoneNumber(), null, null);
    testOnNumber(receivedCall, "Withheld");
  }

  @Test
  public void testOnNumberContactNamed() {
    ReceivedCall receivedCall =
        new ReceivedCall(new Date(), new CountriesOnlyPhoneNumber("44", "800111222", null), "Brian", null);
    testOnNumber(receivedCall, "Brian");
  }

  @Test
  public void testOnNumberContactNotNamed() {
    List<Contact> contacts = Lists.newArrayList(new Contact("Brian May"), new Contact("Freddie Mercury"));
    ReceivedCall receivedCall =
        new ReceivedCall(new Date(), new CountriesOnlyPhoneNumber("44", "800111222", null), null, contacts);
    testOnNumber(receivedCall, "Brian May");
  }

  @Test
  public void testOnNumberForeignNonGeographic() {
    List<Country> countryList =
        Lists.newArrayList(new Country("France", new CountryCode("33"), "fr"), new Country("Sweden", new CountryCode(
            "33"), "se"));
    SortedSet<Country> countries = Sets.newTreeSet(Ordering.explicit(countryList));
    countries.addAll(countryList);
    ReceivedCall receivedCall =
        new ReceivedCall(
            new Date(),
            new CountriesOnlyPhoneNumber("33", "800111222", countries),
            null,
            new ArrayList<Contact>());
    testOnNumber(receivedCall, "+33 800111222 (France)");
  }

  @Test
  public void testOnNumberNonGeographic() {
    List<Country> countryList = Lists.newArrayList(new Country("UK", new CountryCode("44"), "uk"));
    SortedSet<Country> countries = Sets.newTreeSet(Ordering.explicit(countryList));
    countries.addAll(countryList);
    ReceivedCall receivedCall =
        new ReceivedCall(
            new Date(),
            new CountriesOnlyPhoneNumber("44", "800111222", countries),
            null,
            new ArrayList<Contact>());
    testOnNumber(receivedCall, "0800111222 (UK)");
  }

  @Test
  public void testOnNumberGeographic() {
    List<Country> countryList = Lists.newArrayList(new Country("UK", new CountryCode("44"), "uk"));
    SortedSet<Country> countries = Sets.newTreeSet(Ordering.explicit(countryList));
    countries.addAll(countryList);
    ReceivedCall receivedCall =
        new ReceivedCall(new Date(), new CountryAndAreaPhoneNumber(new AreaCode(new Country(
            "UK",
            new CountryCode("44"),
            "uk"), "Basingstoke", "1256"), "555666"), null, new ArrayList<Contact>());
    testOnNumber(receivedCall, "01256 555666 (Basingstoke, UK)");
  }

  @Test
  public void testOnRing() throws IOException {
    squeezeboxCallListener.setMessageToDisplay("Message");
    when(squeezeboxCli.countPlayers()).thenReturn(2);
    boolean result = squeezeboxCallListener.onRing();
    Assert.assertEquals("The wrong value was returned from the onRing() method.", true, result);
    verify(squeezeboxCli).countPlayers();
    verify(squeezeboxCli).display(0, "Incoming call", "Message", 30);
    verify(squeezeboxCli).display(1, "Incoming call", "Message", 30);
    verify(squeezeboxCli).close();
    verifyNoMoreInteractions(squeezeboxCli);
  }
  
  public void testOnNumber(ReceivedCall receivedCall, String expectedMessage) {
    boolean result = squeezeboxCallListener.onNumber(receivedCall);
    Assert.assertEquals("The wrong value was returned from the onNumber() method.", true, result);
    Assert.assertEquals(
        "The wrong message was generated.",
        expectedMessage,
        squeezeboxCallListener.getMessageToDisplay());
  }
}
