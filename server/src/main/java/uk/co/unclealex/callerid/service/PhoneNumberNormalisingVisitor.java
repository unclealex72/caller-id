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

import javax.inject.Inject;

import uk.co.unclealex.callerid.defaults.DefaultsService;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor.Default;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;

/**
 * A link {@link Visitor} that transforms a {@link PhoneNumber} and normalises
 * it. A normalised phone number contains the international prefix, the area
 * code and then the local number. The following table shows normalisation
 * examples, assuming the telephone receiving calls is in Guildford (01483), UK
 * (+44):
 * 
 * <table border="1">
 * <tr align="right">
 * <th>Raw number</th>
 * <th>Normalised Number</th>
 * </tr>
 * <tr align="right">
 * <td>703217</td>
 * <td>441483703217</td>
 * </tr>
 * <tr align="right">
 * <td>0800999666</td>
 * <td>44800999666</td>
 * </tr>
 * <tr align="right">
 * <td>0033987654321</td>
 * <td>333987654321</td>
 * </tr>
 * </table>
 * 
 * {@link WithheldPhoneNumber}s return <code>null<code>.
 * @author alex
 * 
 */
public class PhoneNumberNormalisingVisitor extends Default<String> {

  /**
   * The {@link DefaultsService} used to get default configuration values.
   */
  private final DefaultsService defaultsService;

  /**
   * Instantiates a new phone number normalising visitor.
   * 
   * @param defaultsService
   *          the defaults service
   */
  @Inject
  public PhoneNumberNormalisingVisitor(DefaultsService defaultsService) {
    super();
    this.defaultsService = defaultsService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
    return getDefaultsService().getCountryCode()
        + getDefaultsService().getAreaCode()
        + numberOnlyPhoneNumber.getNumber();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
    return countriesOnlyPhoneNumber.getCountryCode() + countriesOnlyPhoneNumber.getNumber();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
    return countryAndAreaPhoneNumber.getCountryCode()
        + countryAndAreaPhoneNumber.getAreaCode()
        + countryAndAreaPhoneNumber.getNumber();
  }

  /**
   * Gets the {@link DefaultsService} used to get default configuration values.
   * 
   * @return the {@link DefaultsService} used to get default configuration
   *         values
   */
  public DefaultsService getDefaultsService() {
    return defaultsService;
  }

}
