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
import java.util.Collections;
import java.util.List;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor;

/**
 * A {@link Visitor} that converts a {@link PhoneNumber} into a list of
 * strings indicating where the phone number originated.
 * 
 * @author alex
 * 
 */
public class PhoneNumberLocationVisitor extends PhoneNumber.Visitor.Default<List<String>> {

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
    return Collections.singletonList(countriesOnlyPhoneNumber.getCountries().first().getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
    AreaCode areaCode = countryAndAreaPhoneNumber.getAreaCode();
    return Arrays.asList(areaCode.getArea(), areaCode.getCountry().getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
    return null;
  }
}