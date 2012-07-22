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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.unclealex.callerid.areacode.model.AreaCode;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class JdoAreaCodeDaoTest {

  AreaCodeDao areaCodeDao;

  @Before
  public void setUp() throws Exception {
    AreaCodeDaoFactory areaCodeDaoFactory = new AreaCodeDaoFactory(null, getInitialData());
    areaCodeDao = areaCodeDaoFactory.getObject();
  }

  public void testGetAll() {
    List<AreaCode> allAreaCodes = areaCodeDao.getAll();
    checkEqual(getInitialData(), allAreaCodes);
  }

  @Test
  public void testGetAllCountryCodes() {
    List<String> expectedCountryCodes = Lists.newArrayList("247", "297", "93", "1");
    SortedSet<String> actualCountryCodes = areaCodeDao.getAllCountryCodes();
    Assert.assertArrayEquals(
        "The wrong country codes were returned.",
        expectedCountryCodes.toArray(),
        actualCountryCodes.toArray());
  }

  @Test
  public void testGetAllCountriesForCountryCode() {
    testCountries("247", "Ascension Island");
    testCountries("297", "Aruba");
    testCountries("93", "Afghanistan");
    testCountries("1", "United States", "American Samoa");
  }

  @Test
  public void testGetAllAreaCodesForCountryCode() {
    testCountryCode("247")
        .expectAreaCodes("Ascension Island", "Travellers Hill", 30, 31, 32, 33, 34, 35)
        .expectAreaCodes("Ascension Island", "Two Boats", 44, 45, 46)
        .expectAreaCodes("Ascension Island", "U.S. Base", 2)
        .expectAreaCodes("Ascension Island", "Georgetown", 6)
        .test();
    testCountryCode("297").test();
    testCountryCode("93")
        .expectAreaCodes("Afghanistan", "Kabul", 20)
        .expectAreaCodes("Afghanistan", "Parwan", 21)
        .test();
    testCountryCode("1")
        .expectAreaCodes("United States", "District Of Columbia", 202)
        .expectAreaCodes("United States", "Connecticut", 203)
        .expectAreaCodes("American Samoa", "American Samoa", 684)
        .expectAreaCodes("United States", "New Jersey", 20)
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

    public CountryCodeTest expectAreaCodes(String country, String town, int areaCode, int... extraAreaCodes) {
      addExpectedAreaCode(country, town, areaCode);
      for (int extraAreaCode : extraAreaCodes) {
        addExpectedAreaCode(country, town, extraAreaCode);
      }
      return this;
    }

    protected void addExpectedAreaCode(String country, String town, int code) {
      AreaCode areaCode = areaCode(country, town, Integer.toString(code));
      expectedAreaCodes.add(areaCode);
    }

    protected AreaCode areaCode(String country, String town, String code) {
      AreaCode areaCode = new AreaCode();
      areaCode.setArea(town);
      areaCode.setAreaCode(code);
      areaCode.setCountry(country);
      areaCode.setCountryCode(countryCode);
      return areaCode;
    }

    public void test() {
      Collection<AreaCode> actualAreaCodes = areaCodeDao.getAllAreaCodesForCountryCode(countryCode);
      checkEqual(expectedAreaCodes, actualAreaCodes);
    }
  }

  protected void testCountries(String countryCode, String... expectedCountries) {
    SortedSet<String> allCountriesForCountryCode = areaCodeDao.getAllCountriesForCountryCode(countryCode);
    Assert.assertArrayEquals(
        "The wrong countries were returned for country code " + countryCode,
        expectedCountries,
        allCountriesForCountryCode.toArray(new String[0]));
  }

  public List<AreaCode> getInitialData() {
    return Lists.newArrayList(
        areaCode("Afghanistan", "93", "Kabul", "20"),
        areaCode("Afghanistan", "93", "Parwan", "21"),
        areaCode("Afghanistan", "93"),
        areaCode("American Samoa", "1", "American Samoa", "684"),
        areaCode("American Samoa", "1"),
        areaCode("Aruba", "297"),
        areaCode("Ascension Island", "247", "U.S. Base", "2"),
        areaCode("Ascension Island", "247", "Georgetown", "6"),
        areaCode("Ascension Island", "247", "Travellers Hill", "30"),
        areaCode("Ascension Island", "247", "Travellers Hill", "31"),
        areaCode("Ascension Island", "247", "Travellers Hill", "32"),
        areaCode("Ascension Island", "247", "Travellers Hill", "33"),
        areaCode("Ascension Island", "247", "Travellers Hill", "34"),
        areaCode("Ascension Island", "247", "Travellers Hill", "35"),
        areaCode("Ascension Island", "247", "Two Boats", "44"),
        areaCode("Ascension Island", "247", "Two Boats", "45"),
        areaCode("Ascension Island", "247", "Two Boats", "46"),
        areaCode("Ascension Island", "247"),
        areaCode("United States", "1", "New Jersey", "20"),
        areaCode("United States", "1", "District Of Columbia", "202"),
        areaCode("United States", "1", "Connecticut", "203"),
        areaCode("United States", "1"));
  }

  /**
   * Create a new country code.
   * 
   * @param country
   *          The name of the country.
   * @param countryCode
   *          The code for the country.
   * @return An {@link AreaCode} for a country.
   */
  protected AreaCode areaCode(String country, String countryCode) {
    AreaCode areaCode = new AreaCode();
    areaCode.setCountry(country);
    areaCode.setCountryCode(countryCode);
    return areaCode;
  }

  /**
   * Create a new area code.
   * 
   * @param country
   *          The name of the country.
   * @param countryCode
   *          The code for the country.
   * @param area
   *          The name of the area.
   * @param code
   *          The area code for the area.
   * @return A new {@link AreaCode}.
   */
  protected AreaCode areaCode(String country, String countryCode, String area, String code) {
    AreaCode areaCode = areaCode(country, countryCode);
    areaCode.setArea(area);
    areaCode.setAreaCode(code);
    return areaCode;
  }

  protected void checkEqual(Collection<AreaCode> expectedAreaCodes, Collection<AreaCode> actualAreaCodes) {
    Function<AreaCode, AreaCode> idRemovingFunction = new Function<AreaCode, AreaCode>() {
      @Override
      public AreaCode apply(AreaCode areaCode) {
        return areaCode(areaCode.getCountry(), areaCode.getCountryCode(), areaCode.getArea(), areaCode.getAreaCode());
      }
    };
    if (expectedAreaCodes.isEmpty()) {
      Assert.assertEquals("The wrong number of area codes were returned.", 0, actualAreaCodes.size());
    }
    else {
      Assert.assertThat(
          "The wrong area codes were returned.",
          Collections2.transform(actualAreaCodes, idRemovingFunction),
          Matchers.contains(expectedAreaCodes.toArray(new AreaCode[0])));
    }
  }

}
