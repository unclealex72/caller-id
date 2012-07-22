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
import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class PhoneNumberFactoryImplTest {

  PhoneNumberFactoryImpl phoneNumberFactory;

  @Before
  public void test() {
    DefaultsService defaultsService = mock(DefaultsService.class);
    AreaCodeDao areaCodeDao = mock(AreaCodeDao.class);
    when(defaultsService.getCountryCode()).thenReturn("44");
    when(areaCodeDao.getAllCountryCodes()).thenReturn(list("33", "44"));
    when(areaCodeDao.getAllCountriesForCountryCode("33")).thenReturn(list("France"));
    when(areaCodeDao.getAllAreaCodesForCountryCode("44")).thenReturn(
        list(
            areaCode("33", "1", "France", "Paris")));
    when(areaCodeDao.getAllCountriesForCountryCode("44")).thenReturn(list("United Kingdom", "Jersey"));
    when(areaCodeDao.getAllAreaCodesForCountryCode("44")).thenReturn(
        list(
            areaCode("44", "12567", "Jersey", "Newport"),
            areaCode("44", "1256", "United Kingdom", "Basingstoke")));
    phoneNumberFactory = new PhoneNumberFactoryImpl(areaCodeDao, defaultsService);
  }

  protected AreaCode areaCode(String countryCode, String areaCode, String country, String area) {
    AreaCode ac = new AreaCode();
    ac.setCountryCode(countryCode);
    ac.setAreaCode(areaCode);
    ac.setCountry(country);
    ac.setArea(area);
    return ac;
  }

  protected <E> SortedSet<E> list(@SuppressWarnings("unchecked") E... elements) {
    List<E> els = Arrays.asList(elements);
    SortedSet<E> ss = Sets.newTreeSet(Ordering.explicit(els));
    ss.addAll(els);
    return ss;
  }

  @Test
  public void testLocalNumber() {
    runTest("444555", new NumberOnlyPhoneNumber("444555"));
  }

  @Test
  public void testNonGeographicNationalNumber() {
    runTest("0800444555", new CountriesOnlyPhoneNumber("44", "800444555", "United Kingdom", "Jersey"));
  }

  @Test
  public void testNonGeographicInternationalNumber() {
    runTest("0033800444555", new CountriesOnlyPhoneNumber("33", "800444555", "France"));
  }

  @Test
  public void testNationalGeographicNumber() {
    runTest("01256999666", new CountryAndAreaPhoneNumber("United Kingdom", "Basingstoke", "44", "1256", "999666"));
    runTest("01256799666", new CountryAndAreaPhoneNumber("Jersey", "Newport", "44", "12567", "99666"));
  }
  
  @Test
  public void testInternationalGeographicNumber() {
    runTest("00441256999666", new CountryAndAreaPhoneNumber("United Kingdom", "Basingstoke", "44", "1256", "999666"));
    runTest("00441256799666", new CountryAndAreaPhoneNumber("Jersey", "Newport", "44", "12567", "99666"));
  }

  public void runTest(String number, PhoneNumber expectedPhoneNumber) {
    PhoneNumber phoneNumber = phoneNumberFactory.create(number);
    Assert.assertEquals("The wrong phone number was returned for number " + number, expectedPhoneNumber, phoneNumber);
  }
}
