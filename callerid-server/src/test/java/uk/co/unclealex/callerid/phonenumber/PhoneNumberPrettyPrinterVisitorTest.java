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

package uk.co.unclealex.callerid.phonenumber;

import java.util.List;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;
import uk.co.unclealex.callerid.areacode.model.CountryCode;
import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;

import com.google.common.base.Function;

/**
 * @author alex
 * 
 */
public class PhoneNumberPrettyPrinterVisitorTest {

  PhoneNumberPrettyPrinterVisitor phoneNumberPrettyPrinterVisitor;

  public void setup() {

  }

  @Test
  public void testLocal() {
    runTest(new NumberOnlyPhoneNumber("703217"), "01483", "703217");
  }

  @Test
  public void testFullyQualifiedLocal() {
    final CountryCode countryCode = new CountryCode("44");
    final Country uk = new Country("United Kingdom", countryCode, "uk");
    final AreaCode guildford = new AreaCode(uk, "Guildford", "1483");
    runTest(new CountryAndAreaPhoneNumber("703217", guildford), "01483", "703217");
  }

  @Test
  public void testFullyQualifiedNational() {
    final CountryCode countryCode = new CountryCode("44");
    final Country uk = new Country("United Kingdom", countryCode, "uk");
    final AreaCode basingstoke = new AreaCode(uk, "Basingstoke", "1256");
    runTest(new CountryAndAreaPhoneNumber("703217", basingstoke), "01256", "703217");
  }

  @Test
  public void testNonGeographicNational() {
    runTest(new CountriesOnlyPhoneNumber("800999666", new TreeSet<Country>(), "44"), "0800999666");
  }

  @Test
  public void testNonGeographicInternational() {
    runTest(new CountriesOnlyPhoneNumber("800999666", new TreeSet<Country>(), "33"), "+33", "800999666");
  }

  @Test
  public void testInternational() {
    final CountryCode countryCode = new CountryCode("33");
    final Country france = new Country("France", countryCode, "fr");
    final AreaCode basingstoke = new AreaCode(france, "Basingstoke", "1256");
    runTest(new CountryAndAreaPhoneNumber("703217", basingstoke), "+33", "1256", "703217");
  }

  @Test
  public void testWithheld() {
    Assert.assertNull("A withheld number was not pretty printed as null.", new PhoneNumberFunction<>(
        new PhoneNumberPrettyPrinterVisitor(null)).apply(new WithheldPhoneNumber()));
  }

  protected void runTest(final PhoneNumber phoneNumber, final String... expectedParts) {
    final DefaultsService defaultsService = Mockito.mock(DefaultsService.class);
    Mockito.when(defaultsService.getAreaCodePrefix()).thenReturn("0");
    Mockito.when(defaultsService.getAreaCode()).thenReturn("1483");
    Mockito.when(defaultsService.getCountryCode()).thenReturn("44");
    final Function<PhoneNumber, List<String>> f =
        new PhoneNumberFunction<>(new PhoneNumberPrettyPrinterVisitor(defaultsService));
    final String[] actualParts = f.apply(phoneNumber).toArray(new String[0]);
    Assert.assertArrayEquals(
        "The wrong pretty printed phone number was returned for phone number " + phoneNumber,
        expectedParts,
        actualParts);
  }
}
