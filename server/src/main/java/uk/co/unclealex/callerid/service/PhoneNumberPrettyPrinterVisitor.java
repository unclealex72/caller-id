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

package uk.co.unclealex.callerid.service;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor.Default;

/**
 * A class that pretty prints a telephone number. A pretty printed number is
 * returned as a list of strings so consumers are free to split the constituent
 * parts of the pretty printed number as they please. The following table shows
 * pretty print examples, assuming the telephone receiving calls is in Guildford
 * (01483), UK (+44):
 * 
 * <table border="1">
 * <tr align="right">
 * <th>Raw number</th>
 * <th>Normalised Number</th>
 * </tr>
 * <tr align="right">
 * <td>703217</td>
 * <td>01483 703217</td>
 * </tr>
 * <tr align="right">
 * <td>00441483703217</td>
 * <td>01483 703217</td>
 * </tr>
 * <tr align="right">
 * <td>00441256703217</td>
 * <td>01256 703217</td>
 * </tr>
 * <tr align="right">
 * <td>0800999666</td>
 * <td>0800 999666</td>
 * </tr>
 * <tr align="right">
 * <td>0033987654321</td>
 * <td>+33 3987 654321</td>
 * </tr>
 * </table>
 * 
 * @author alex
 * 
 */
public class PhoneNumberPrettyPrinterVisitor extends Default<List<String>> {

  /**
   * The {@link DefaultsService} used to get configuration defaults.
   */
  private final DefaultsService defaultsService;

  /**
   * @param defaultsService
   */
  public PhoneNumberPrettyPrinterVisitor(DefaultsService defaultsService) {
    super();
    this.defaultsService = defaultsService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
    return nationalNumber(getDefaultsService().getAreaCode(), numberOnlyPhoneNumber.getNumber());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
    if (isNational(countriesOnlyPhoneNumber.getCountryCode())) {
      return Lists.newArrayList(getDefaultsService().getAreaCodePrefix() + countriesOnlyPhoneNumber.getNumber());
    }
    else {
      return internationalNumber(countriesOnlyPhoneNumber.getCountryCode(), countriesOnlyPhoneNumber.getNumber());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
    if (isNational(countryAndAreaPhoneNumber.getCountryCode())) {
      return Lists.newArrayList(
          getDefaultsService().getAreaCodePrefix() + countryAndAreaPhoneNumber.getAreaCode(),
          countryAndAreaPhoneNumber.getNumber());
    }
    else {
      return internationalNumber(
          countryAndAreaPhoneNumber.getCountryCode(),
          countryAndAreaPhoneNumber.getAreaCode(),
          countryAndAreaPhoneNumber.getNumber());
    }
  }

  protected boolean isNational(String countryCode) {
    return countryCode.equals(getDefaultsService().getCountryCode());
  }
  
  protected List<String> internationalNumber(String countryCode, String... otherParts) {
    List<String> parts = Lists.newArrayList("+" + countryCode);
    parts.addAll(Arrays.asList(otherParts));
    return parts;
  }

  protected List<String> nationalNumber(String areaCode, String number) {
    return Lists.newArrayList(getDefaultsService().getAreaCodePrefix() + areaCode, number);
  }

  public DefaultsService getDefaultsService() {
    return defaultsService;
  }

}
