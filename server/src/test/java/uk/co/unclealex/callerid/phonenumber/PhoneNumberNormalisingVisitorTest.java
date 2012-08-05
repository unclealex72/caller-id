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

import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;
import uk.co.unclealex.callerid.areacode.model.CountryCode;
import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberFunction;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberNormalisingVisitor;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;

/**
 * @author alex
 *
 */
public class PhoneNumberNormalisingVisitorTest {

  @Test
  public void testLocal() {
    runTest(new NumberOnlyPhoneNumber("472901"), "441256472901");
  }
 
  @Test
  public void testFullGeographic() {
    CountryCode fr = new CountryCode("33");
    Country france = new Country("France", fr, "fr");
    AreaCode paris = new AreaCode(france, "Paris", "1");
    runTest(new CountryAndAreaPhoneNumber(paris, "472902"), "331472902");
  }
 
  @Test
  public void testNonLocalNonGeographic() {
    runTest(new CountriesOnlyPhoneNumber("1", "555800122", new TreeSet<Country>()), "1555800122");
  }

  @Test
  public void testWithheld() {
    Assert.assertNull("A withheld number was not normalised to null.", new PhoneNumberFunction<>(
        new PhoneNumberNormalisingVisitor(null)).apply(new WithheldPhoneNumber()));
  }


  public void runTest(PhoneNumber phoneNumber, String expectedNormalisedNumber) {
    DefaultsService defaultsService = Mockito.mock(DefaultsService.class);
    Mockito.when(defaultsService.getCountryCode()).thenReturn("44");
    Mockito.when(defaultsService.getAreaCode()).thenReturn("1256");
    PhoneNumberFunction<String> phoneNumberFunction = new PhoneNumberFunction<>(new PhoneNumberNormalisingVisitor(defaultsService));
    String actualNormalisedNumber = phoneNumberFunction.apply(phoneNumber);
    Assert.assertEquals("The wrong normalised number was returned for phone number " + phoneNumber, expectedNormalisedNumber, actualNormalisedNumber);
  }
}
