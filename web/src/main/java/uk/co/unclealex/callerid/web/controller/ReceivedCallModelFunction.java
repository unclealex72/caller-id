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

package uk.co.unclealex.callerid.web.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.model.ReceivedCallModel;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor;

import com.google.common.base.Function;
import com.mycila.inject.internal.guava.base.Joiner;

/**
 * A {@link Function} that translates a {@link Call} into a.
 * 
 * {@link ReceivedCallModel}.
 * 
 * @author alex
 */
public class ReceivedCallModelFunction implements Function<ReceivedCall, ReceivedCallModel> {

  /**
   * The {@link PhoneNumber} pretty printer.
   */
  private final Function<PhoneNumber, List<String>> phoneNumberPrettyPrinter;

  @Inject
  public ReceivedCallModelFunction(
      @Named("phoneNumberPrettyPrinter") Function<PhoneNumber, List<String>> phoneNumberPrettyPrinter) {
    this.phoneNumberPrettyPrinter = phoneNumberPrettyPrinter;
  }

  /**
   * {@inheritDoc}
   */
  public ReceivedCallModel apply(ReceivedCall receivedCall) {
    PhoneNumber phoneNumber = receivedCall.getPhoneNumber();
    List<String> prettyPrintedPhoneNumber = getPhoneNumberPrettyPrinter().apply(phoneNumber);
    List<Contact> contacts = receivedCall.getContacts();
    String contactName;
    String explicitContactName = receivedCall.getContactName();
    if (explicitContactName != null) {
      contactName = explicitContactName;
    }
    else {
      contactName = (contacts == null || contacts.isEmpty()) ? null : contacts.get(0).getName();
    }
    List<String> location = phoneNumber.accept(new PhoneNumberLocationVisitor());
    String googleMapsSearchTerm = phoneNumber.accept(new GoogleMapsSearchTermLocationVisitor());
    String googleMapsSearchArea = phoneNumber.accept(new GoogleMapsSearchAreaLocationVisitor());
    String googleSearchTerm =
        prettyPrintedPhoneNumber == null || contactName != null ? null : Joiner.on("").join(prettyPrintedPhoneNumber);
    boolean editable = prettyPrintedPhoneNumber != null && contactName == null;
    return new ReceivedCallModel(
        receivedCall.getStartTime(),
        arrayOf(prettyPrintedPhoneNumber),
        contactName,
        arrayOf(location),
        googleMapsSearchTerm,
        googleMapsSearchArea,
        googleSearchTerm,
        editable);
  }

  /**
   * Convert a list of strings to an array of strings.
   * 
   * @param data
   *          The strings to return in the array.
   * @return The data as an array or the null if the data is null.
   */
  protected String[] arrayOf(List<String> data) {
    return data == null ? null : data.toArray(new String[data.size()]);
  }

  /**
   * A {@link Visitor} that converts a {@link PhoneNumber} into a list of
   * strings indicating where the phone number originated.
   * 
   * @author alex
   * 
   */
  class PhoneNumberLocationVisitor extends PhoneNumber.Visitor.Default<List<String>> {

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

  /**
   * A {@link Visitor} that gets the search term to use in Google maps for a
   * {@link PhoneNumber}.
   * 
   * @author alex
   * 
   */
  class GoogleMapsSearchTermLocationVisitor extends PhoneNumber.Visitor.Default<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
      return countriesOnlyPhoneNumber.getCountries().first().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
      return countryAndAreaPhoneNumber.getAreaCode().getArea();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
      return null;
    }

  }

  /**
   * A {@link Visitor} that gets the search area to use in Google maps for a
   * {@link PhoneNumber}.
   * 
   * @author alex
   * 
   */
  class GoogleMapsSearchAreaLocationVisitor extends PhoneNumber.Visitor.Default<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
      return countriesOnlyPhoneNumber.getCountries().first().getTld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
      return countryAndAreaPhoneNumber.getAreaCode().getCountry().getTld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
      return null;
    }

  }

  public Function<PhoneNumber, List<String>> getPhoneNumberPrettyPrinter() {
    return phoneNumberPrettyPrinter;
  }
}
