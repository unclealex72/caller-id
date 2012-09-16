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

package uk.co.unclealex.callerid.web.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.junit.Test;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;
import uk.co.unclealex.callerid.areacode.model.CountryCode;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.model.ReceivedCallModel;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.mycila.inject.internal.guava.collect.Lists;
import com.mycila.inject.internal.guava.collect.Sets;

/**
 * @author alex
 * 
 */
public class ReceivedCallModelFunctionTest {

  static Date CALL_RECEIVED_TIME = new Date();

  class TestCase {

    private final PhoneNumber phoneNumber;
    private String explicitContact;
    private List<Contact> contacts = Lists.newArrayList();
    private String[] prettyPrintedPhoneNumber;
    private String expectedContact;
    private String[] expectedLocation;
    private String expectedMapsSearchTerm;
    private String expectedMapsSearchArea;
    private String expectedSearchTerm;

    /**
     * @param phoneNumber
     */
    public TestCase(PhoneNumber phoneNumber) {
      super();
      this.phoneNumber = phoneNumber;
    }

    public void expectingEditable() {
      test(true);
    }

    public void expectingNotEditable() {
      test(false);
    }

    public TestCase withExplicitContact(String explicitContact) {
      this.explicitContact = explicitContact;
      return this;
    }

    public TestCase withContacts(String... contactNames) {
      Function<String, Contact> f = new Function<String, Contact>() {
        public Contact apply(String contactName) {
          return new Contact(contactName, new ArrayList<String>());
        }
      };
      this.contacts = Lists.newArrayList(Iterables.transform(Arrays.asList(contactNames), f));
      return this;
    }

    public TestCase prettyPrintedTo(String... prettyPrintedNumber) {
      this.prettyPrintedPhoneNumber = prettyPrintedNumber;
      return this;
    }

    public TestCase expectingContact(String expectedContact) {
      this.expectedContact = expectedContact;
      return this;
    }

    public TestCase expectingLocation(String... expectedLocation) {
      this.expectedLocation = expectedLocation;
      return this;
    }

    public TestCase expectingMapsSearchTerm(String expectedMapsSearchTerm) {
      this.expectedMapsSearchTerm = expectedMapsSearchTerm;
      return this;
    }

    public TestCase expectingMapsSearchArea(String expectedMapsSearchArea) {
      this.expectedMapsSearchArea = expectedMapsSearchArea;
      return this;
    }

    public TestCase expectingSearchTerm(String expectedSearchTerm) {
      this.expectedSearchTerm = expectedSearchTerm;
      return this;
    }

    public void test(boolean expectedEditable) {
      Function<PhoneNumber, List<String>> phoneNumberPrettyPrinter = new Function<PhoneNumber, List<String>>() {
        public List<String> apply(PhoneNumber phoneNumber) {
          return prettyPrintedPhoneNumber == null ? null : Arrays.asList(prettyPrintedPhoneNumber);
        }
      };
      ReceivedCallModelFunction receivedCallModelFunction = new ReceivedCallModelFunction(phoneNumberPrettyPrinter);
      Date now = new Date();
      ReceivedCall receivedCall = new ReceivedCall(now, phoneNumber, explicitContact, contacts);
      ReceivedCallModel receivedCallModel = receivedCallModelFunction.apply(receivedCall);
      assertEquals("The model had the wrong call time.", now, receivedCallModel.getCallTime());
      assertEquals("The model had the wrong contact name.", expectedContact, receivedCallModel.getContact());
      assertEquals(
          "The model had the wrong maps search area.",
          expectedMapsSearchArea,
          receivedCallModel.getGoogleMapsSearchArea());
      assertEquals(
          "The model had the wrong maps search term.",
          expectedMapsSearchTerm,
          receivedCallModel.getGoogleMapsSearchTerm());
      assertEquals("The model had the wrong search term.", expectedSearchTerm, receivedCallModel.getGoogleSearchTerm());
      assertArrayEquals("The model had the wrong location.", expectedLocation, receivedCallModel.getLocation());
      assertArrayEquals(
          "The model had the pretty printed phone number.",
          prettyPrintedPhoneNumber,
          receivedCallModel.getPrettyPrintedPhoneNumber());
      assertEquals("The model had the wrong editable flag.", expectedEditable, receivedCallModel.isEditable());
    }

  }

  @Test
  public void testCountriesOnlyPhoneNumber() {
    testCountriesOnlyPhoneNumber("uk", "44", "800118118", "United Kingdom", "Jersey")
        .withExplicitContact("Brian")
        .withContacts("Freddie", "John")
        .prettyPrintedTo("0800118118")
        .expectingContact("Brian")
        .expectingLocation("United Kingdom")
        .expectingMapsSearchTerm("United Kingdom")
        .expectingMapsSearchArea("uk")
        .expectingNotEditable();
  }

  @Test
  public void testCountryAndAreaPhoneNumber() {
    testCountryAndAreaPhoneNumber("us", "United States", "New York", "1", "212", "1234567")
        .prettyPrintedTo("00", "1", "212", "1234567")
        .expectingLocation("New York", "United States")
        .expectingMapsSearchTerm("New York")
        .expectingMapsSearchArea("us")
        .expectingSearchTerm("0012121234567")
        .expectingEditable();
  }

  @Test
  public void testNumberOnlyPhoneNumber() {
    testNumberOnlyPhoneNumber("100100").prettyPrintedTo("100", "100").expectingSearchTerm("100100").expectingEditable();
  }

  @Test
  public void testWithheldPhoneNumber() {
    new TestCase(new WithheldPhoneNumber()).expectingNotEditable();
  }

  TestCase testCountriesOnlyPhoneNumber(
      final String tld,
      final String countryCode,
      String number,
      String... countryNames) {
    Function<String, Country> f = new Function<String, Country>() {
      public Country apply(String countryName) {
        return new Country(countryName, new CountryCode(countryCode), tld);
      }
    };
    Iterable<Country> countries = Iterables.transform(Arrays.asList(countryNames), f);

    return new TestCase(new CountriesOnlyPhoneNumber(countryCode, number, sorted(countries)));
  }

  TestCase testCountryAndAreaPhoneNumber(
      String tld,
      String countryName,
      String area,
      String countryCode,
      String areaCode,
      String number) {
    Country country = new Country(countryName, new CountryCode(countryCode), tld);
    CountryAndAreaPhoneNumber countryAndAreaPhoneNumber =
        new CountryAndAreaPhoneNumber(new AreaCode(country, area, areaCode), number);
    return new TestCase(countryAndAreaPhoneNumber);
  }

  TestCase testNumberOnlyPhoneNumber(String number) {
    NumberOnlyPhoneNumber numberOnlyPhoneNumber = new NumberOnlyPhoneNumber(number);
    return new TestCase(numberOnlyPhoneNumber);
  }

  protected <E> SortedSet<E> sorted(Iterable<E> elements) {
    Ordering<E> explicitOrdering = Ordering.explicit(Lists.newArrayList(elements));
    SortedSet<E> sortedElements = Sets.newTreeSet(explicitOrdering);
    Iterables.addAll(sortedElements, elements);
    return sortedElements;
  }

}
