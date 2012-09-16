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

package uk.co.unclealex.callerid.areacode.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;
import uk.co.unclealex.callerid.areacode.model.CountryCode;

import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class InMemoryAreaCodeDaoTest {

  InMemoryAreaCodeDao areaCodeDao;

  @Before
  public void setUp() throws Exception {
    AreaCodeFactory areaCodeFactory = new AreaCodeFactory() {
      @Override
      public List<AreaCode> createAreaCodes() {
        return getInitialData();
      }
    };
    areaCodeDao = new InMemoryAreaCodeDao(areaCodeFactory);
    areaCodeDao.loadAndComputeCountrySizes();
  }

  public void testGetAll() {
    List<AreaCode> allAreaCodes = areaCodeDao.getAll();
    checkEqual(getInitialData(), allAreaCodes);
  }

  @Test
  public void testGetAllCountryCodes() {
    List<String> expectedCountryCodes = Lists.newArrayList("247", "93", "1");
    SortedSet<String> actualCountryCodes = areaCodeDao.getAllCountryCodes();
    Assert.assertArrayEquals(
        "The wrong country codes were returned.",
        expectedCountryCodes.toArray(),
        actualCountryCodes.toArray());
  }

  @Test
  public void testGetAllCountriesForCountryCode() {
    CountryCode ai = new CountryCode("247");
    testCountries("247", new Country("Ascension Island", ai, "ai"));
    CountryCode af = new CountryCode("93");
    testCountries("93", new Country("Afghanistan", af, "af"));
    CountryCode us = new CountryCode("1");
    testCountries("1", new Country("United States", us, "us"), new Country("American Samoa", us, "as"));
  }

  @Test
  public void testGetAllAreaCodesForCountryCodeAscensionIsland() {
    testCountryCode("247")
        .expectAreaCodes("Ascension Island", "ai", "Travellers Hill", 30, 31, 32, 33, 34, 35)
        .expectAreaCodes("Ascension Island", "ai", "Two Boats", 44, 45, 46)
        .expectAreaCodes("Ascension Island", "ai", "U.S. Base", 2)
        .expectAreaCodes("Ascension Island", "ai", "Georgetown", 6)
        .test();
  }

  @Test
  public void testGetAllAreaCodesForCountryCodeAfghanistan() {
    testCountryCode("93")
        .expectAreaCodes("Afghanistan", "af", "Kabul", 20)
        .expectAreaCodes("Afghanistan", "af", "Parwan", 21)
        .test();
  }

  @Test
  public void testGetAllAreaCodesForCountryCodeUnitedStates() {
    testCountryCode("1")
        .expectAreaCodes("United States", "us", "District Of Columbia", 202)
        .expectAreaCodes("United States", "us", "Connecticut", 203)
        .expectAreaCodes("American Samoa", "as", "American Samoa", 684)
        .expectAreaCodes("United States", "us", "New Jersey", 20)
        .test();
  }

  protected CountryCodeTest testCountryCode(String countryCode) {
    return new CountryCodeTest(countryCode);
  }

  class CountryCodeTest {

    private final String countryCode;
    private final List<AreaCode> expectedAreaCodes = Lists.newArrayList();

    /**
     * @param countryCode
     */
    public CountryCodeTest(String countryCode) {
      this.countryCode = countryCode;
    }

    public
        CountryCodeTest
        expectAreaCodes(String country, String tld, String town, int... areaCodes) {
      for (int areaCode : areaCodes) {
        addExpectedAreaCode(country, tld, town, areaCode);
      }
      return this;
    }

    protected void addExpectedAreaCode(String countryName, String tld, String town, int code) {
      CountryCode countryCode = new CountryCode(this.countryCode);
      Country country = new Country(countryName, countryCode, tld);
      AreaCode areaCode = new AreaCode(country, town, Integer.toString(code));
      expectedAreaCodes.add(areaCode);
    }

    public void test() {
      Collection<AreaCode> actualAreaCodes = areaCodeDao.getAllAreaCodesForCountryCode(countryCode);
      checkEqual(expectedAreaCodes, actualAreaCodes);
    }
  }

  protected void testCountries(String countryCode, Country... expectedCountries) {
    SortedSet<Country> allCountriesForCountryCode = areaCodeDao.getAllCountriesForCountryCode(countryCode);
    Assert.assertArrayEquals(
        "The wrong countries were returned for country code " + countryCode,
        expectedCountries,
        allCountriesForCountryCode.toArray(new Country[0]));
  }

  protected <E> void checkEqual(Collection<AreaCode> expectedAreaCodes, Collection<AreaCode> actualAreaCodes) {
    Assert.assertArrayEquals(
        "The wrong area codes were returned.",
        expectedAreaCodes.toArray(new AreaCode[0]),
        actualAreaCodes.toArray(new AreaCode[0]));
  }

  public List<AreaCode> getInitialData() {
    return countryCode("1")
        .withCountry("American Samoa", "as")
        .addArea("American Samoa", "684")
        .withCountry("United States", "us")
        .addArea("New Jersey", "20")
        .addArea("District Of Columbia", "202")
        .addArea("Connecticut", "203")
        .withCountryCode("93")
        .withCountry("Afghanistan", "af")
        .addArea("Kabul", "20")
        .addArea("Parwan", "21")
        .withCountryCode("247")
        .withCountry("Ascension Island", "ai")
        .addArea("U.S. Base", "2")
        .addArea("Georgetown", "6")
        .addArea("Travellers Hill", "30")
        .addArea("Travellers Hill", "31")
        .addArea("Travellers Hill", "32")
        .addArea("Travellers Hill", "33")
        .addArea("Travellers Hill", "34")
        .addArea("Travellers Hill", "35")
        .addArea("Two Boats", "44")
        .addArea("Two Boats", "45")
        .addArea("Two Boats", "46");
  }

  protected AreaCodeList countryCode(String countryCode) {
    return new AreaCodeList().withCountryCode(countryCode);
  }

  protected class AreaCodeList extends ArrayList<AreaCode> {

    CountryCode countryCode;
    Country country;

    public AreaCodeList withCountryCode(String countryCode) {
      this.countryCode = new CountryCode(countryCode);
      return this;
    }

    public AreaCodeList withCountry(String countryName, String tld) {
      this.country = new Country(countryName, this.countryCode, tld);
      return this;
    }

    public AreaCodeList addArea(String areaName, String areaCode) {
      add(new AreaCode(this.country, areaName, areaCode));
      return this;
    }
  }

}
