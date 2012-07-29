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

package uk.co.unclealex.callerid.phonenumber.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.callerid.areacode.dao.AreaCodeDao;
import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;
import uk.co.unclealex.callerid.areacode.model.CountryCode;
import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;
import uk.co.unclealex.callerid.service.PhoneNumberFactoryImpl;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class PhoneNumberFactoryImplTest {

  PhoneNumberFactoryImpl phoneNumberFactory;
  DefaultsService defaultsService;
  AreaCodeDao areaCodeDao;
  CountryCode fr = new CountryCode("33");
  CountryCode uk = new CountryCode("44");
  Country unitedKingdom = new Country("United Kingdom", uk, "uk");
  Country jersey = new Country("Jersey", uk, "je");
  Country france = new Country("France", fr, "fr");
  AreaCode paris = new AreaCode(france, "Paris", "1");
  AreaCode newport = new AreaCode(jersey, "Newport", "12567");
  AreaCode basingstoke = new AreaCode(unitedKingdom, "Basingstoke", "1256");

  @Before
  public void test() {
    defaultsService = mock(DefaultsService.class);
    areaCodeDao = mock(AreaCodeDao.class);
    when(defaultsService.getCountryCode()).thenReturn("44");
    when(areaCodeDao.getAllCountryCodes()).thenReturn(list("33", "44"));
    when(areaCodeDao.getAllCountriesForCountryCode("33")).thenReturn(list(france));
    when(areaCodeDao.getAllAreaCodesForCountryCode("44")).thenReturn(list(paris));
    when(areaCodeDao.getAllCountriesForCountryCode("44")).thenReturn(list(unitedKingdom, jersey));
    when(areaCodeDao.getAllAreaCodesForCountryCode("44")).thenReturn(list(newport, basingstoke));
    phoneNumberFactory = new PhoneNumberFactoryImpl(areaCodeDao, defaultsService);
  }

  protected <E> SortedSet<E> list(@SuppressWarnings("unchecked") E... elements) {
    List<E> els = Arrays.asList(elements);
    SortedSet<E> ss = Sets.newTreeSet(Ordering.explicit(els));
    ss.addAll(els);
    return ss;
  }

  @Test
  public void testLocalNumber() {
    runTest("00", "0", "444555", new NumberOnlyPhoneNumber("444555"));
  }

  @Test
  public void testNonGeographicNationalNumber() {
    runTest("00", "1", "1800444555", new CountriesOnlyPhoneNumber("44", "800444555", list(unitedKingdom, jersey)));
  }

  @Test
  public void testNonGeographicInternationalNumber() {
    runTest("00", "000", "0033800444555", new CountriesOnlyPhoneNumber("33", "800444555", list(france)));
  }

  @Test
  public void testNationalGeographicNumber() {
    runTest("00", "0", "01256999666", new CountryAndAreaPhoneNumber(basingstoke, "999666"));
    runTest("00", "0", "01256799666", new CountryAndAreaPhoneNumber(newport, "99666"));
  }

  @Test
  public void testInternationalGeographicNumber() {
    runTest("00", "0", "00441256999666", new CountryAndAreaPhoneNumber(basingstoke, "999666"));
    runTest("00", "0", "00441256799666", new CountryAndAreaPhoneNumber(newport, "99666"));
  }

  @Test
  public void testWithheld() {
    PhoneNumber phoneNumber = phoneNumberFactory.create(null);
    Assert.assertEquals(
        "The wrong phone number was returned for a null number.",
        new WithheldPhoneNumber(),
        phoneNumber);
  }

  public
      void
      runTest(String internationalPrefix, String areaCodePrefix, String number, PhoneNumber expectedPhoneNumber) {
    when(defaultsService.getInternationalPrefix()).thenReturn(internationalPrefix);
    when(defaultsService.getAreaCodePrefix()).thenReturn(areaCodePrefix);
    PhoneNumber phoneNumber = phoneNumberFactory.create(number);
    Assert.assertEquals("The wrong phone number was returned for number " + number, expectedPhoneNumber, phoneNumber);
  }
}
