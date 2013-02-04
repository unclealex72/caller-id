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
    public TestCase(final PhoneNumber phoneNumber) {
      super();
      this.phoneNumber = phoneNumber;
    }

    public void expectingEditable() {
      test(true);
    }

    public void expectingNotEditable() {
      test(false);
    }

    public TestCase withExplicitContact(final String explicitContact) {
      this.explicitContact = explicitContact;
      return this;
    }

    public TestCase withContacts(final String... contactNames) {
      final Function<String, Contact> f = new Function<String, Contact>() {
        @Override
        public Contact apply(final String contactName) {
          return new Contact(contactName);
        }
      };
      this.contacts = Lists.newArrayList(Iterables.transform(Arrays.asList(contactNames), f));
      return this;
    }

    public TestCase prettyPrintedTo(final String... prettyPrintedNumber) {
      this.prettyPrintedPhoneNumber = prettyPrintedNumber;
      return this;
    }

    public TestCase expectingContact(final String expectedContact) {
      this.expectedContact = expectedContact;
      return this;
    }

    public TestCase expectingLocation(final String... expectedLocation) {
      this.expectedLocation = expectedLocation;
      return this;
    }

    public TestCase expectingMapsSearchTerm(final String expectedMapsSearchTerm) {
      this.expectedMapsSearchTerm = expectedMapsSearchTerm;
      return this;
    }

    public TestCase expectingMapsSearchArea(final String expectedMapsSearchArea) {
      this.expectedMapsSearchArea = expectedMapsSearchArea;
      return this;
    }

    public TestCase expectingSearchTerm(final String expectedSearchTerm) {
      this.expectedSearchTerm = expectedSearchTerm;
      return this;
    }

    public void test(final boolean expectedEditable) {
      final Function<PhoneNumber, List<String>> phoneNumberPrettyPrinter = new Function<PhoneNumber, List<String>>() {
        @Override
        public List<String> apply(final PhoneNumber phoneNumber) {
          return prettyPrintedPhoneNumber == null ? null : Arrays.asList(prettyPrintedPhoneNumber);
        }
      };
      final ReceivedCallModelFunction receivedCallModelFunction =
          new ReceivedCallModelFunction(phoneNumberPrettyPrinter);
      final Date now = new Date();
      final ReceivedCall receivedCall = new ReceivedCall(now, phoneNumber, explicitContact, contacts);
      final ReceivedCallModel receivedCallModel = receivedCallModelFunction.apply(receivedCall);
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
      final String number,
      final String... countryNames) {
    final Function<String, Country> f = new Function<String, Country>() {
      @Override
      public Country apply(final String countryName) {
        return new Country(countryName, new CountryCode(countryCode), tld);
      }
    };
    final Iterable<Country> countries = Iterables.transform(Arrays.asList(countryNames), f);

    return new TestCase(new CountriesOnlyPhoneNumber(number, sorted(countries), countryCode));
  }

  TestCase testCountryAndAreaPhoneNumber(
      final String tld,
      final String countryName,
      final String area,
      final String countryCode,
      final String areaCode,
      final String number) {
    final Country country = new Country(countryName, new CountryCode(countryCode), tld);
    final CountryAndAreaPhoneNumber countryAndAreaPhoneNumber =
        new CountryAndAreaPhoneNumber(number, new AreaCode(country, area, areaCode));
    return new TestCase(countryAndAreaPhoneNumber);
  }

  TestCase testNumberOnlyPhoneNumber(final String number) {
    final NumberOnlyPhoneNumber numberOnlyPhoneNumber = new NumberOnlyPhoneNumber(number);
    return new TestCase(numberOnlyPhoneNumber);
  }

  protected <E> SortedSet<E> sorted(final Iterable<E> elements) {
    final Ordering<E> explicitOrdering = Ordering.explicit(Lists.newArrayList(elements));
    final SortedSet<E> sortedElements = Sets.newTreeSet(explicitOrdering);
    Iterables.addAll(sortedElements, elements);
    return sortedElements;
  }

}
