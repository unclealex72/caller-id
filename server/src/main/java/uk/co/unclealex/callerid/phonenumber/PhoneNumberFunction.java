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

import javax.annotation.Nullable;

import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor;

import com.google.common.base.Function;

/**
 * An adaptor to used to convert a {@link PhoneNumber.Visitor} into a function.
 * 
 * @param <E>
 *          the element type
 * @author alex
 */
public class PhoneNumberFunction<E> implements Function<PhoneNumber, E> {

  /**
   * The {@link PhoneNumber.Visitor} that will be used to transform the {@link PhoneNumber}.
   */
  private final PhoneNumber.Visitor<E> phoneNumberVisitor;
  
  /**
   * Instantiates a new phone number function.
   * 
   * @param phoneNumberVisitor
   *          the phone number visitor
   */
  public PhoneNumberFunction(Visitor<E> phoneNumberVisitor) {
    super();
    this.phoneNumberVisitor = phoneNumberVisitor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public E apply(PhoneNumber phoneNumber) {
    return phoneNumber.accept(getPhoneNumberVisitor());
  }

  /**
   * Gets the {@link PhoneNumber.Visitor} that will be used to transform the {@link PhoneNumber}.
   *
   * @return the {@link PhoneNumber.Visitor} that will be used to transform the {@link PhoneNumber}.
   */
  public PhoneNumber.Visitor<E> getPhoneNumberVisitor() {
    return phoneNumberVisitor;
  }

}
