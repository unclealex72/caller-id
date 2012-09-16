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

import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.WithheldPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor;

/**
 * A {@link Visitor} that is used to see if a {@link PhoneNumber} has been withheld.
 * @author alex
 *
 */
public class PhoneNumberWithheldVisitor extends PhoneNumber.Visitor.Default<Boolean> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean visit(WithheldPhoneNumber withheldPhoneNumber) {
    return true;
  }
}
