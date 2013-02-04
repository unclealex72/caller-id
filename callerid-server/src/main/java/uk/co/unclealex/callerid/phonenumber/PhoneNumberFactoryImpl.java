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

import java.util.Arrays;
import java.util.SortedSet;

import javax.inject.Inject;

import uk.co.unclealex.callerid.areacode.dao.AreaCodeDao;
import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

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

  /**
   * The {@link DefaultsService} used to get all defaults for the user's current
   * location.
   */
  private final DefaultsService defaultsService;

  /**
   * @param areaCodeDao
   * @param defaultsService
   */
  @Inject
  public PhoneNumberFactoryImpl(final AreaCodeDao areaCodeDao, final DefaultsService defaultsService) {
    super();
    this.areaCodeDao = areaCodeDao;
    this.defaultsService = defaultsService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PhoneNumber create(final String number) {
    if (number == null) {
      return new WithheldPhoneNumber();
    }
    final String internationalPrefix = getDefaultsService().getInternationalPrefix();
    final String areaCodePrefix = getDefaultsService().getAreaCodePrefix();
    final MatchResult<String> matchResult = match(number, internationalPrefix, areaCodePrefix);
    if (matchResult == null) {
      return localNumber(number);
    }
    else {
      final String matchedPrefix = matchResult.getMatch();
      final String remainingNumber = matchResult.getRemainingNumber();
      if (internationalPrefix.equals(matchedPrefix)) {
        return internationalNumber(remainingNumber);
      }
      else {
        return nationalNumber(remainingNumber);
      }
    }
  }

  /**
   * Parse an international number.
   * 
   * @param number
   *          The number to parse.
   * @return A non-local phone number for potentially a list of countries.
   */
  protected PhoneNumber internationalNumber(final String number) {
    final AreaCodeDao areaCodeDao = getAreaCodeDao();
    final MatchResult<String> countryCodeMatchResult = match(number, areaCodeDao.getAllCountryCodes());
    final String countryCode = countryCodeMatchResult.getMatch();
    final String nationalNumber = countryCodeMatchResult.getRemainingNumber();
    return nonLocalPhoneNumber(countryCode, nationalNumber);
  }

  /**
   * Parse a national phone number.
   * 
   * @param number
   *          The number to parse.
   * @return A non-local phone number for the country of residence.
   */
  protected PhoneNumber nationalNumber(final String number) {
    return nonLocalPhoneNumber(getDefaultsService().getCountryCode(), number);
  }

  /**
   * Parse a non-local phone number.
   * 
   * @param countryCode
   *          The country code of the number.
   * @param nationalNumber
   *          The national part of the phone number minus any national area code
   *          prefix.
   * @return A non-local phone number which may or may not be geographic.
   */
  protected PhoneNumber nonLocalPhoneNumber(final String countryCode, final String nationalNumber) {
    final AreaCodeDao areaCodeDao = getAreaCodeDao();
    final Function<AreaCode, String> areaCodeFunction = new Function<AreaCode, String>() {
      @Override
      public String apply(final AreaCode areaCode) {
        return areaCode.getAreaCode();
      }
    };
    final MatchResult<AreaCode> areaCodeMatchResult =
        match(nationalNumber, areaCodeFunction, areaCodeDao.getAllAreaCodesForCountryCode(countryCode));
    if (areaCodeMatchResult == null) {
      // We have a non-geographic international number.
      return new CountriesOnlyPhoneNumber(
          nationalNumber,
          areaCodeDao.getAllCountriesForCountryCode(countryCode),
          countryCode);
    }
    else {
      final AreaCode areaCode = areaCodeMatchResult.getMatch();
      final String localNumber = areaCodeMatchResult.getRemainingNumber();
      return new CountryAndAreaPhoneNumber(localNumber, areaCode);
    }
  }

  /**
   * Parse a local number.
   * 
   * @param number
   *          The whole dialled phone number.
   * @return A local phone number.
   */
  protected PhoneNumber localNumber(final String number) {
    return new NumberOnlyPhoneNumber(number);
  }

  /**
   * Attempt to match a phone number against a list of possible prefixes.
   * 
   * @param number
   *          The number to check.
   * @param prefixes
   *          A list of prefixes to check to see if the number starts with one.
   * @return A {@link MatchResult} containing the prefixed matched and the
   *         remainder of the string or null if no prefix was matched.
   */
  protected MatchResult<String> match(final String number, final String... prefixes) {
    return match(number, Arrays.asList(prefixes));
  }

  /**
   * Attempt to match a phone number against a list of possible prefixes.
   * 
   * @param number
   *          The number to check.
   * @param prefixes
   *          A list of prefixes to check to see if the number starts with one.
   * @return A {@link MatchResult} containing the prefixed matched and the
   *         remainder of the string or null if no prefix was matched.
   */
  protected MatchResult<String> match(final String number, final Iterable<String> prefixes) {
    final Function<String, Integer> lengthFunction = new Function<String, Integer>() {
      @Override
      public Integer apply(final String str) {
        return str.length();
      }
    };
    final SortedSet<String> longestFirstPrefixes =
        Sets.newTreeSet(Ordering.natural().reverse().onResultOf(lengthFunction).compound(Ordering.natural()));
    Iterables.addAll(longestFirstPrefixes, prefixes);
    final Function<String, String> f = Functions.identity();
    return match(number, f, longestFirstPrefixes);
  }

  /**
   * Attempt to match a phone number against a list of possible objects.
   * 
   * @param number
   *          The number to check.
   * @param matchFunction
   *          A {@link Function} that turns an object into a prefix to match
   *          against.
   * @param matchers
   *          The objects that will be transformed into prefixes to check for.
   * @return A {@link MatchResult} containing the object matched and the
   *         remainder of the string or null if no prefix was matched.
   */
  protected <E> MatchResult<E> match(
      final String number,
      final Function<E, String> matchFunction,
      final Iterable<? extends E> matchers) {
    final Predicate<E> matcherPredicate = new Predicate<E>() {
      @Override
      public boolean apply(final E match) {
        return number.startsWith(matchFunction.apply(match));
      }
    };
    final E match = Iterables.find(matchers, matcherPredicate, null);
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
    public MatchResult(final E match, final String remainingNumber) {
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
