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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * An in-memory implementation of {@link AreaCodeDao}.
 * 
 * @author alex
 */
public class InMemoryAreaCodeDao implements AreaCodeDao {

  /** The string length function. */
  static Function<String, Integer> STRING_LENGTH_FUNCTION = new Function<String, Integer>() {
    @Override
    public Integer apply(String str) {
      return str.length();
    }
  };

  /** The area code to international prefix function. */
  static Function<AreaCode, String> AREA_CODE_TO_INTERNATIONAL_PREFIX_FUNCTION = new Function<AreaCode, String>() {
    public String apply(AreaCode areaCode) {
      return areaCode.getCountry().getCountryCode().getInternationalPrefix();
    }
  };

  /**
   * The {@link AreaCodeFactory} used to get all known {@link AreaCode}s.
   */
  private final AreaCodeFactory areaCodeFactory;
  
  /** The area codes. */
  private List<AreaCode> areaCodes;

  /** The area code count by country. */
  private final Map<Country, Integer> areaCodeCountByCountry = Maps.newHashMap();

  /**
   * Instantiates a new in memory area code dao.
   * 
   * @param areaCodeFactory
   *          the area codes
   */
  @Inject
  public InMemoryAreaCodeDao(AreaCodeFactory areaCodeFactory) {
    super();
    this.areaCodeFactory = areaCodeFactory;
  }

  /**
   * Compute country sizes.
   */
  @PostConstruct
  public void loadAndComputeCountrySizes() {
    List<AreaCode> areaCodes = getAreaCodeFactory().createAreaCodes();
    Map<Country, Integer> areaCodeCountByCountry = getAreaCodeCountByCountry();
    for (AreaCode areaCode : areaCodes) {
      Country country = areaCode.getCountry();
      Integer currentSize = areaCodeCountByCountry.get(country);
      int newSize = currentSize == null ? 1 : (currentSize + 1);
      areaCodeCountByCountry.put(country, newSize);
    }
    setAreaCodes(areaCodes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<String> getAllCountryCodes() {
    Function<String, Integer> sizeFunction = STRING_LENGTH_FUNCTION;
    Function<AreaCode, String> countryCodeFunction = new Function<AreaCode, String>() {
      public String apply(AreaCode areaCode) {
        return areaCode.getCountry().getCountryCode().getInternationalPrefix();
      }
    };
    Comparator<String> backupComparator = Ordering.natural();
    Predicate<AreaCode> filter = Predicates.alwaysTrue();
    return sortedSetOf(filter, countryCodeFunction, sizeFunction, backupComparator, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<Country> getAllCountriesForCountryCode(String countryCode) {
    Function<Country, Integer> sizeFunction = Functions.forMap(getAreaCodeCountByCountry());
    Function<AreaCode, Country> countryFunction = new Function<AreaCode, Country>() {
      public Country apply(AreaCode areaCode) {
        return areaCode.getCountry();
      }
    };
    Function<Country, String> countryNameFunction = new Function<Country, String>() {
      public String apply(Country country) {
        return country.getName();
      }
    };
    Comparator<Country> backupComparator = Ordering.natural().onResultOf(countryNameFunction);
    Predicate<AreaCode> filter =
        Predicates.compose(Predicates.equalTo(countryCode), AREA_CODE_TO_INTERNATIONAL_PREFIX_FUNCTION);
    return sortedSetOf(filter, countryFunction, sizeFunction, backupComparator, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<AreaCode> getAllAreaCodesForCountryCode(String countryCode) {
    Function<AreaCode, String> areaCodeFunction = new Function<AreaCode, String>() {
      public String apply(AreaCode areaCode) {
        return areaCode.getAreaCode();
      }
    };
    Function<AreaCode, Integer> sizeFunction = Functions.compose(STRING_LENGTH_FUNCTION, areaCodeFunction);
    Comparator<AreaCode> backupComparator = Ordering.natural().onResultOf(areaCodeFunction);
    Function<AreaCode, AreaCode> identityFunction = Functions.identity();
    Predicate<AreaCode> filter =
        Predicates.compose(Predicates.equalTo(countryCode), AREA_CODE_TO_INTERNATIONAL_PREFIX_FUNCTION);
    return sortedSetOf(filter, identityFunction, sizeFunction, backupComparator, false);
  }

  /**
   * Create a sorted set of elements based originally on {@link #getAreaCodes()}
   * .
   * 
   * @param <E>
   *          the element type
   * @param filter
   *          A {@link Predicate} to indicate which area codes will be used to
   *          source the resulting sorted set.
   * @param resultFunction
   *          A {@link Function} that turns an {@link AreaCode} into the result
   *          type to be included in the final resulting sorted set.
   * @param sizingFunction
   *          A {@link Function} used to size each result and thus be the basis
   *          for the {@link Comparator} for the resulting sorted set.
   * @param backupComparator
   *          The {@link Comparator} to use for elements that have the same
   *          size.
   * @param ascending
   *          True if results should be sorted in ascending order, false
   *          otherwise.
   * @return A sorted set sourced as above.
   */
  protected <E> SortedSet<E> sortedSetOf(
      Predicate<AreaCode> filter,
      Function<AreaCode, E> resultFunction,
      Function<E, Integer> sizingFunction,
      Comparator<E> backupComparator,
      boolean ascending) {
    Iterable<AreaCode> filteredAreaCodes = Iterables.filter(getAreaCodes(), filter);
    Iterable<E> unsortedResults = Iterables.transform(filteredAreaCodes, resultFunction);
    Ordering<Integer> ordering = Ordering.natural();
    if (!ascending) {
      ordering = ordering.reverse();
    }
    Comparator<E> comparator = ordering.onResultOf(sizingFunction).compound(backupComparator);
    SortedSet<E> sortedResults = Sets.newTreeSet(comparator);
    Iterables.addAll(sortedResults, unsortedResults);
    return sortedResults;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AreaCode> getAll() {
    return getAreaCodes();
  }

  /**
   * Gets the area codes.
   * 
   * @return the area codes
   */
  public List<AreaCode> getAreaCodes() {
    return areaCodes;
  }

  /**
   * Gets the area code count by country.
   * 
   * @return the area code count by country
   */
  public Map<Country, Integer> getAreaCodeCountByCountry() {
    return areaCodeCountByCountry;
  }

  public AreaCodeFactory getAreaCodeFactory() {
    return areaCodeFactory;
  }

  public void setAreaCodes(List<AreaCode> areaCodes) {
    this.areaCodes = areaCodes;
  }

}
