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

import javax.inject.Inject;
import javax.jdo.PersistenceManagerFactory;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.QAreaCode;
import uk.co.unclealex.persistence.jdo.JdoBasicDao;
import uk.co.unclealex.persistence.paging.PagingService;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mysema.query.jdo.JDOQLQuery;

/**
 * The JDO implementation of {@link AreaCodeDao}.
 * 
 * @author alex
 * 
 */
public class JdoAreaCodeDao extends JdoBasicDao<AreaCode, QAreaCode> implements AreaCodeDao {

  static Function<String, Integer> STRING_LENGTH_FUNCTION = new Function<String, Integer>() {
    @Override
    public Integer apply(String str) {
      return str.length();
    }
  };

  static Function<AreaCode, String> AREACODE_FUNCTION = new Function<AreaCode, String>() {
    @Override
    public String apply(AreaCode areaCode) {
      return areaCode.getAreaCode();
    }
  };

  /**
   * Create a {@link Comparator} that compares strings by first applying a
   * sizing function and comparing the results. If the two strings have the same
   * size then they are compared as normal.
   * 
   * @param sizeFunction
   *          The function that will supply a string's size.
   * @param smallestFirst
   *          True if smaller sizes should be first, false otherwise.
   * @return A new {@link Comparator}.
   */
  protected Comparator<String> createComparator(final Function<String, Integer> sizeFunction, boolean smallestFirst) {
    final int sign = smallestFirst ? 1 : -1;
    Comparator<String> comparator = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        int cmp = sizeFunction.apply(o1).compareTo(sizeFunction.apply(o2)) * sign;
        if (cmp == 0) {
          cmp = o1.compareTo(o2);
        }
        return cmp;
      }
    };
    return comparator;
  }

  /**
   * 
   * @param persistenceManagerFactory
   * @param pagingService
   */
  @Inject
  public JdoAreaCodeDao(PersistenceManagerFactory persistenceManagerFactory, PagingService pagingService) {
    super(persistenceManagerFactory, pagingService);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<String> getAllCountryCodes() {
    QueryCallback<SortedSet<String>> callback = new QueryCallback<SortedSet<String>>() {
      @Override
      public SortedSet<String> doInQuery(JDOQLQuery query) {
        QAreaCode areaCode = QAreaCode.areaCode1;
        return toSortedSet(query.from(areaCode).listDistinct(areaCode.countryCode), STRING_LENGTH_FUNCTION, false);
      }
    };
    return execute(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<String> getAllCountriesForCountryCode(final String countryCode) {
    QueryCallback<SortedSet<String>> callback = new QueryCallback<SortedSet<String>>() {
      @Override
      public SortedSet<String> doInQuery(JDOQLQuery query) {
        QAreaCode areaCode = QAreaCode.areaCode1;
        List<Object[]> countriesWithAreaCodeCount =
            query
                .from(areaCode)
                .where(areaCode.countryCode.eq(countryCode))
                .groupBy(areaCode.country)
                .orderBy(areaCode.area.count().desc())
                .list(areaCode.country, areaCode.area.count());
        Map<String, Integer> sizeMap = Maps.newHashMap();
        for (Object[] objs : countriesWithAreaCodeCount) {
          String country = (String) objs[0];
          int count = ((Long) objs[1]).intValue();
          sizeMap.put(country, count);
        }
        return toSortedSet(sizeMap.keySet(), Functions.forMap(sizeMap), false);
      }
    };
    return execute(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<AreaCode> getAllAreaCodesForCountryCode(final String countryCode) {
    QueryCallback<SortedSet<AreaCode>> callback = new QueryCallback<SortedSet<AreaCode>>() {
      @Override
      public SortedSet<AreaCode> doInQuery(JDOQLQuery query) {
        QAreaCode areaCode = QAreaCode.areaCode1;
        List<AreaCode> results = query
            .from(areaCode)
            .where(areaCode.countryCode.eq(countryCode).and(areaCode.areaCode.isNotNull()))
            .list(areaCode);
        return toSortedSet(
            results,
            AREACODE_FUNCTION,
            STRING_LENGTH_FUNCTION,
            false);
      }
    };
    return execute(callback);
  }

  /**
   * Create a {@link SortedSet} from a {@link Iterable} and a sizing function.
   * 
   * @param iterable
   *          The source of elements.
   * @param sizeFunction
   *          A {@link Function} used to measure each element.
   * @param smallestFirst
   *          True if the smallest element should be first or false otherwise.
   * @return A {@link SortedSet} with all the original elements ordered
   *         accordingly.
   */
  protected SortedSet<String> toSortedSet(
      Iterable<String> iterable,
      Function<String, Integer> sizeFunction,
      boolean smallestFirst) {
    Function<String, String> f = Functions.identity();
    return toSortedSet(iterable, f, sizeFunction, smallestFirst);
  }

  /**
   * Create a {@link SortedSet} from a {@link Iterable} and a sizing function.
   * 
   * @param iterable
   *          The source of elements.
   * @param stringExtractingFunction
   *          A function that extracts the string to be measured.
   * @param sizeFunction
   *          A {@link Function} used to measure each element.
   * @param smallestFirst
   *          True if the smallest element should be first or false otherwise.
   * @return A {@link SortedSet} with all the original elements ordered
   *         accordingly.
   */
  protected <E> SortedSet<E> toSortedSet(
      Iterable<E> iterable,
      Function<E, String> stringExtractingFunction,
      Function<String, Integer> sizeFunction,
      boolean smallestFirst) {
    Ordering<String> stringOrdering = Ordering.from(createComparator(sizeFunction, smallestFirst));
    Ordering<E> ordering = stringOrdering.onResultOf(stringExtractingFunction);
    SortedSet<E> ss = Sets.newTreeSet(ordering);
    Iterables.addAll(ss, iterable);
    return ss;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QAreaCode candidate() {
    return QAreaCode.areaCode1;
  }

}
