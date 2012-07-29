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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.callerid.calls.dao.CallDao;
import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.phonenumber.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.service.ReceivedCallFactory;
import uk.co.unclealex.persistence.paging.Page;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A controller for listing all recieved calls.
 * 
 * @author alex
 * 
 */
@RequestMapping("/c")
@Controller
public class CallsController {

  /**
   * The {@link CallDao} used to list received calls.
   */
  private final CallDao callDao;

  /**
   * The {@link ReceivedCallFactory} used to get all received call information.
   */
  private final ReceivedCallFactory receivedCallFactory;

  /**
   * The {@link PhoneNumber} pretty printer.
   */
  private final Function<PhoneNumber, List<String>> phoneNumberPrettyPrinter;

  /**
   * @param callDao
   * @param receivedCallFactory
   * @param phoneNumberPrettyPrinter
   */
  @Inject
  public CallsController(
      CallDao callDao,
      ReceivedCallFactory receivedCallFactory,
      @Named("phoneNumberPrettyPrinter") Function<PhoneNumber, List<String>> phoneNumberPrettyPrinter) {
    super();
    this.callDao = callDao;
    this.receivedCallFactory = receivedCallFactory;
    this.phoneNumberPrettyPrinter = phoneNumberPrettyPrinter;
  }

  @RequestMapping(value = "/calls.html", method = RequestMethod.GET)
  public ModelAndView listCalls() {
    return listCalls(1, 10);
  }

  @RequestMapping(value = "/{page}/{size}/calls.html", method = RequestMethod.GET)
  public ModelAndView listCalls(@PathVariable("page") int page, @PathVariable("size") int size) {
    ModelAndView mav = new ModelAndView("calls");
    Page<Call> callsByTimeReceived = getCallDao().pageAllByTimeReceived(page, size);
    Iterable<ReceivedCallModel> receivedCalls =
        Iterables.transform(callsByTimeReceived.getElements(), new ReceivedCallFunction());
    mav.getModel().put("calls", Lists.newArrayList(receivedCalls));
    return mav;
  }

  class ReceivedCallFunction implements Function<Call, ReceivedCallModel> {

    /**
     * {@inheritDoc}
     */
    public ReceivedCallModel apply(Call call) {
      ReceivedCall receivedCall = getReceivedCallFactory().create(call);
      PhoneNumber phoneNumber = receivedCall.getPhoneNumber();
      List<String> prettyPrintedPhoneNumber = getPhoneNumberPrettyPrinter().apply(phoneNumber);
      List<Contact> contacts = receivedCall.getContacts();
      String contactName = contacts.isEmpty() ? null : contacts.get(0).getName();
      List<String> location = phoneNumber.accept(new PhoneNumberLocationVisitor());
      String googleSearchTerm = phoneNumber.accept(new GoogleSearchTermLocationVisitor());
      String googleSearchArea = phoneNumber.accept(new GoogleSearchAreaLocationVisitor());
      return new ReceivedCallModel(
          receivedCall.getStartTime(),
          prettyPrintedPhoneNumber,
          contactName,
          location,
          googleSearchTerm,
          googleSearchArea);
    }
  }

  class PhoneNumberLocationVisitor extends PhoneNumber.Visitor.Default<List<String>> {

    @Override
    public List<String> visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
      return Collections.singletonList(countriesOnlyPhoneNumber.getCountries().first().getName());
    }

    @Override
    public List<String> visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
      AreaCode areaCode = countryAndAreaPhoneNumber.getAreaCode();
      return Arrays.asList(areaCode.getArea(), areaCode.getCountry().getName());
    }

    @Override
    public List<String> visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
      return null;
    }
  }

  class GoogleSearchTermLocationVisitor extends PhoneNumber.Visitor.Default<String> {

    @Override
    public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
      return countriesOnlyPhoneNumber.getCountries().first().getName();
    }

    @Override
    public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
      return countryAndAreaPhoneNumber.getAreaCode().getArea();
    }

    @Override
    public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
      return null;
    }

  }

  class GoogleSearchAreaLocationVisitor extends PhoneNumber.Visitor.Default<String> {

    @Override
    public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
      return countriesOnlyPhoneNumber.getCountries().first().getTld();
    }

    @Override
    public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
      return countryAndAreaPhoneNumber.getAreaCode().getCountry().getTld();
    }

    @Override
    public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
      return null;
    }

  }

  public CallDao getCallDao() {
    return callDao;
  }

  public ReceivedCallFactory getReceivedCallFactory() {
    return receivedCallFactory;
  }

  public Function<PhoneNumber, List<String>> getPhoneNumberPrettyPrinter() {
    return phoneNumberPrettyPrinter;
  }
}
