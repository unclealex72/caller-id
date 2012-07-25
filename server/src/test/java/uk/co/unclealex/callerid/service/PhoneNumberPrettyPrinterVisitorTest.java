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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

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
    runTest(new CountryAndAreaPhoneNumber("", "", "44", "1483", "703217"), "01483", "703217");
  }

  @Test
  public void testFullyQualifiedNational() {
    runTest(new CountryAndAreaPhoneNumber("", "", "44", "1256", "703217"), "01256", "703217");
  }

  @Test
  public void testNonGeographicNational() {
    runTest(new CountriesOnlyPhoneNumber("44", "800999666"), "0800999666");
  }

  @Test
  public void testNonGeographicInternational() {
    runTest(new CountriesOnlyPhoneNumber("33", "800999666"), "+33", "800999666");
  }

  @Test
  public void testInternational() {
    runTest(new CountryAndAreaPhoneNumber("", "", "33", "1256", "703217"), "+33", "1256", "703217");
  }

  protected void runTest(PhoneNumber phoneNumber, String... expectedParts) {
    DefaultsService defaultsService = Mockito.mock(DefaultsService.class);
    Mockito.when(defaultsService.getAreaCodePrefix()).thenReturn("0");
    Mockito.when(defaultsService.getAreaCode()).thenReturn("1483");
    Mockito.when(defaultsService.getCountryCode()).thenReturn("44");
    Function<PhoneNumber, List<String>> f =
        new PhoneNumberFunction<>(new PhoneNumberPrettyPrinterVisitor(defaultsService));
    String[] actualParts = f.apply(phoneNumber).toArray(new String[0]);
    Assert.assertArrayEquals(
        "The wrong pretty printed phone number was returned for phone number " + phoneNumber,
        expectedParts,
        actualParts);
  }
}
