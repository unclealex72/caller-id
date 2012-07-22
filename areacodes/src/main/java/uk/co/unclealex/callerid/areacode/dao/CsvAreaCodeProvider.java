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
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.FactoryBean;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

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
public class CsvAreaCodeProvider implements FactoryBean<Iterable<AreaCode>> {

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
  private final String resourceName;

  /**
   * @param resourceName
   */
  @Inject
  public CsvAreaCodeProvider(String resourceName) {
    super();
    this.resourceName = resourceName;
  }

  /**
   * {@inheritDoc}
   * @throws IOException 
   */
  @Override
  public Iterable<AreaCode> getObject() throws IOException {
    try (CSVReader reader = new CSVReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(getResourceName())))) {
      List<String[]> allRows = reader.readAll();
      if (!allRows.isEmpty()) {
        allRows.remove(0);
      }
      Function<String[], AreaCode> f = new Function<String[], AreaCode>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public AreaCode apply(String[] fields) {
          return createAreaCode(fields);
        }
      };
      return Iterables.transform(allRows, f);
    }
  }

  protected AreaCode createAreaCode(String[] fields) {
    if (fields.length != 4) {
      throw new IllegalArgumentException("There are not enough fields in " + Joiner.on(", ").join(fields));
    }
    String countryName = fields[0].trim();
    String countryCode = PREFIX_REMOVING_FUNCTION.apply(fields[1].trim());
    String areaName = Strings.emptyToNull(fields[2].trim());
    String areaCode = PREFIX_REMOVING_FUNCTION.apply(fields[3].trim());
    AreaCode ac = new AreaCode();
    ac.setCountry(countryName);
    ac.setCountryCode(countryCode);
    ac.setArea(areaName);
    ac.setAreaCode(areaCode);
    return ac;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getObjectType() {
    return Iterable.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSingleton() {
    return true;
  }

  public String getResourceName() {
    return resourceName;
  }

}
