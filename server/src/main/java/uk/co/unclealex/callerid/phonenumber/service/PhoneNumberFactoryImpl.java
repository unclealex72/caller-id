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

import java.util.Arrays;

import javax.inject.Inject;

import uk.co.unclealex.callerid.areacode.dao.AreaCodeDao;
import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The default implementation of {@link PhoneNumberFactory}.
 * 
 * @author alex
 * 
 */
public class PhoneNumberFactoryImpl implements PhoneNumberFactory {

  /**
   * The {@link AreaCodeDao} used to look for known area codes.
   */
  private final AreaCodeDao areaCodeDao;

  private final DefaultsService defaultsService;

  /**
   * @param areaCodeDao
   * @param defaultsService
   */
  @Inject
  public PhoneNumberFactoryImpl(AreaCodeDao areaCodeDao, DefaultsService defaultsService) {
    super();
    this.areaCodeDao = areaCodeDao;
    this.defaultsService = defaultsService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PhoneNumber create(String number) {
    MatchResult<String> matchResult = match(number, "00", "0");
    if (matchResult == null) {
      return localNumber(number);
    }
    else {
      String matchedPrefix = matchResult.getMatch();
      String remainingNumber = matchResult.getRemainingNumber();
      if ("00".equals(matchedPrefix)) {
        return internationalNumber(remainingNumber);
      }
      else {
        return nationalNumber(remainingNumber);
      }
    }
  }

  protected PhoneNumber internationalNumber(String number) {
    AreaCodeDao areaCodeDao = getAreaCodeDao();
    MatchResult<String> countryCodeMatchResult = match(number, areaCodeDao.getAllCountryCodes());
    String countryCode = countryCodeMatchResult.getMatch();
    String nationalNumber = countryCodeMatchResult.getRemainingNumber();
    return nonLocalPhoneNumber(countryCode, nationalNumber);
  }

  protected PhoneNumber nationalNumber(String number) {
    return nonLocalPhoneNumber(getDefaultsService().getCountryCode(), number);
  }
  
  protected PhoneNumber nonLocalPhoneNumber(String countryCode, String nationalNumber) {
    AreaCodeDao areaCodeDao = getAreaCodeDao();
    Function<AreaCode, String> areaCodeFunction = new Function<AreaCode, String>() {
      @Override
      public String apply(AreaCode areaCode) {
        return areaCode.getAreaCode();
      }
    };
    MatchResult<AreaCode> areaCodeMatchResult =
        match(nationalNumber, areaCodeFunction, areaCodeDao.getAllAreaCodesForCountryCode(countryCode));
    if (areaCodeMatchResult == null) {
      // We have a non-geographic international number.
      return new CountriesOnlyPhoneNumber(
          countryCode,
          nationalNumber,
          Lists.newArrayList(areaCodeDao.getAllCountriesForCountryCode(countryCode)));
    }
    else {
      AreaCode areaCode = areaCodeMatchResult.getMatch();
      String localNumber = areaCodeMatchResult.getRemainingNumber();
      return new CountryAndAreaPhoneNumber(
          areaCode.getCountry(),
          areaCode.getArea(),
          areaCode.getCountryCode(),
          areaCode.getAreaCode(),
          localNumber);
    }
  }

  protected PhoneNumber localNumber(String number) {
    return new NumberOnlyPhoneNumber(number);
  }
  
  protected MatchResult<String> match(String number, String... matchers) {
    return match(number, Arrays.asList(matchers));
  }

  protected MatchResult<String> match(String number, Iterable<String> matchers) {
    Function<String, String> f = Functions.identity();
    return match(number, f, matchers);
  }

  protected <E> MatchResult<E> match(
      final String number,
      final Function<E, String> matchFunction,
      Iterable<? extends E> matchers) {
    Predicate<E> matcherPredicate = new Predicate<E>() {
      @Override
      public boolean apply(E match) {
        return number.startsWith(matchFunction.apply(match));
      }
    };
    E match = Iterables.find(matchers, matcherPredicate, null);
    return match == null ? null : new MatchResult<E>(match, number.substring(matchFunction.apply(match).length()));
  }

  /**
   * A class to represent the result of trying to match an object to the
   * beginning of a number.
   * 
   * @author alex
   * 
   * @param <E>
   */
  class MatchResult<E> {

    /**
     * The object that matched the beginning of a number.
     */
    private final E match;

    /**
     * The matched number with the matched object removed.
     */
    private final String remainingString;

    /**
     * @param match
     * @param remainingNumber
     */
    public MatchResult(E match, String remainingNumber) {
      super();
      this.match = match;
      this.remainingString = remainingNumber;
    }

    public E getMatch() {
      return match;
    }

    public String getRemainingNumber() {
      return remainingString;
    }

  }

  public AreaCodeDao getAreaCodeDao() {
    return areaCodeDao;
  }

  public DefaultsService getDefaultsService() {
    return defaultsService;
  }
}
