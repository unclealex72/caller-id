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

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.areacode.model.Country;
import uk.co.unclealex.callerid.areacode.model.CountryCode;
import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A {@link FactoryBean} that provides a list of {@link AreaCode}s from a CSV
 * file. The first line is ignored and the rest of the lines are expected to
 * have the following fields:
 * <ol>
 * <li>country name,</li>
 * <li>country code,</li>
 * <li>area name,</li>
 * <li>area code.</li>
 * </ol>
 * 
 * For both code fields, any characters before the last set of digits are
 * ignored. For example <i>(0)1256</i> is read as <i>1256</i>.
 * 
 * @author alex
 * 
 */
public class CsvAreaCodeFactory implements AreaCodeFactory {

  private static final Logger log = LoggerFactory.getLogger(CsvAreaCodeFactory.class);

  static Function<String, String> PREFIX_REMOVING_FUNCTION = new Function<String, String>() {
    @Override
    public String apply(String input) {
      if (input.isEmpty()) {
        return null;
      }
      Integer offset = null;
      for (int idx = input.length() - 1; offset == null && idx >= 0; idx--) {
        if (!Character.isDigit(input.charAt(idx))) {
          offset = idx;
        }
      }
      return offset == null ? input : input.substring(offset + 1);
    }
  };

  /**
   * The name of the resource that contains the area code information.
   */
  private final String areaCodesResourceName;

  /**
   * The name of the resource that contains country tld information.
   */
  private final String ccTldResourceName;

  /**
   * @param areaCodesResourceName
   * @param ccTldResourceName
   */
  @Inject
  public CsvAreaCodeFactory(String areaCodesResourceName, String ccTldResourceName) {
    super();
    this.areaCodesResourceName = areaCodesResourceName;
    this.ccTldResourceName = ccTldResourceName;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws IOException
   */
  @Override
  public List<AreaCode> createAreaCodes() {
    try {
      final Map<String, String> tldCodesByCountryName = readTldCodes();
      Predicate<String[]> validRowPredicate = new Predicate<String[]>() {
        public boolean apply(String[] row) {
          return row.length == 4
              && !Strings.isNullOrEmpty(row[0])
              && !Strings.isNullOrEmpty(row[1])
              && !Strings.isNullOrEmpty(row[2])
              && !Strings.isNullOrEmpty(row[3]);
        }
      };
      List<String[]> allRows = readCsvFile(getAreaCodesResourceName(), validRowPredicate);
      if (!allRows.isEmpty()) {
        allRows.remove(0);
      }
      Function<String[], AreaCode> f = new Function<String[], AreaCode>() {
        @Override
        public AreaCode apply(String[] fields) {
          return createAreaCode(fields, tldCodesByCountryName);
        }
      };
      return Lists.newArrayList(Iterables.transform(allRows, f));
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Read all the information from a CSV resource.
   * 
   * @param resourceName
   *          The name of the classpath resource containing the CSV data.
   * @param rowPredicate
   *          Only rows that match this predicate will be returned.
   * @return A list of string arrays, one element for each cell.
   * @throws IOException
   */
  protected List<String[]> readCsvFile(String resourceName, Predicate<String[]> rowPredicate) throws IOException {
    try (
        CSVReader reader =
            new CSVReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(resourceName),
                Charset.forName("UTF-8")))) {
      return Lists.newArrayList(Iterables.filter(reader.readAll(), rowPredicate));
    }
  }

  protected Map<String, String> readTldCodes() throws IOException {
    Predicate<String[]> validTldPredicate = new Predicate<String[]>() {
      public boolean apply(String[] row) {
        return row.length == 2;
      }
    };
    List<String[]> allRows = readCsvFile(getCcTldResourceName(), validTldPredicate);
    Map<String, String> tldCodesByCountryName = Maps.newTreeMap();
    for (String[] row : allRows) {
      String countryName = row[1].trim();
      String tld = row[0].trim();
      tldCodesByCountryName.put(countryName, tld);
      log.debug(String.format("Added tld code %s for country %s.", tld, countryName));
    }
    return tldCodesByCountryName;
  }

  protected AreaCode createAreaCode(String[] fields, Map<String, String> tldCodesByCountryName) {
    String countryName = fields[0].trim();
    String internationalPrefix = PREFIX_REMOVING_FUNCTION.apply(fields[1].trim());
    String areaName = Strings.emptyToNull(fields[2].trim());
    String areaCode = PREFIX_REMOVING_FUNCTION.apply(fields[3].trim());
    CountryCode countryCode = new CountryCode(internationalPrefix);
    Country country = new Country(countryName, countryCode, tldCodesByCountryName.get(countryName));
    return new AreaCode(country, areaName, areaCode);
  }

  public String getAreaCodesResourceName() {
    return areaCodesResourceName;
  }

  public String getCcTldResourceName() {
    return ccTldResourceName;
  }
}
