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
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber.Visitor;
import uk.co.unclealex.callerid.service.ReceivedCallFactory;
import uk.co.unclealex.persistence.paging.Page;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

// TODO: Auto-generated Javadoc
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
   * The default page size.
   */
  private static final int PAGE_SIZE = 5;

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
   * Instantiates a new calls controller.
   * 
   * @param callDao
   *          the call dao
   * @param receivedCallFactory
   *          the received call factory
   * @param phoneNumberPrettyPrinter
   *          the phone number pretty printer
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

  /**
   * List the first page calls.
   * 
   * @return A model and view for listing the first page of received calls.
   */
  @RequestMapping(value = "/calls.html", method = RequestMethod.GET)
  public ModelAndView listCalls() {
    return listCalls(1);
  }

  /**
   * List a page of calls.
   * 
   * @param page
   *          The 1-based page to display.
   * @return A model and view for listing a page of received calls.
   */
  @RequestMapping(value = "/{page}/calls.html", method = RequestMethod.GET)
  public ModelAndView listCalls(@PathVariable("page") int page) {
    ModelAndView mav = new ModelAndView("calls");
    Page<Call> callsByTimeReceived = getCallDao().pageAllByTimeReceived(page, PAGE_SIZE);
    Iterable<ReceivedCallModel> receivedCalls =
        Iterables.transform(callsByTimeReceived.getElements(), new ReceivedCallFunction());
    mav.getModel().put("calls", Lists.newArrayList(receivedCalls));
    mav.getModel().put("page", createPageModel(callsByTimeReceived, page));
    return mav;
  }

  /**
   * Create a page model.
   * 
   * @param callsByTimeReceived
   *          The page of calls to model.
   * @param currentPage
   *          The number of the page to model.
   * @return A {@link PageModel} that can be used to display information about
   *         the current page.
   */
  protected PageModel createPageModel(Page<Call> callsByTimeReceived, int currentPage) {
    int firstIndex = (int) callsByTimeReceived.getOffset();
    int lastIndex = firstIndex + (int) callsByTimeReceived.getPageSize();
    int totalResultCount = (int) callsByTimeReceived.getTotalElementCount();
    Integer previousPage = currentPage == 1 ? null : currentPage - 1;
    int lastPage = callsByTimeReceived.getPageOffsetsByPageNumber().lastKey().intValue();
    Integer nextPage = currentPage == lastPage ? null : currentPage + 1;
    Function<Long, Integer> f = new Function<Long, Integer>() {
      public Integer apply(Long pageNumber) {
        return pageNumber.intValue();
      }
    };
    List<Integer> allPages =
        Lists.newArrayList(Iterables.transform(callsByTimeReceived.getPageOffsetsByPageNumber().keySet(), f));
    return new PageModel(
        firstIndex,
        lastIndex,
        totalResultCount,
        previousPage,
        nextPage,
        currentPage,
        lastPage,
        allPages);
  }

  /**
   * A {@link Function} that translates a {@link Call} into a.
   * 
   * {@link ReceivedCallModel}.
   * 
   * @author alex
   */
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
          prettyPrintedPhoneNumber.toArray(new String[prettyPrintedPhoneNumber.size()]),
          contactName,
          location.toArray(new String[location.size()]),
          googleSearchTerm,
          googleSearchArea);
    }
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
   * A {@link Visitor} that gets the search term to use in Google maps for a {@link PhoneNumber}.
   * @author alex
   *
   */
  class GoogleSearchTermLocationVisitor extends PhoneNumber.Visitor.Default<String> {

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
   * A {@link Visitor} that gets the search area to use in Google maps for a {@link PhoneNumber}.
   * @author alex
   *
   */
  class GoogleSearchAreaLocationVisitor extends PhoneNumber.Visitor.Default<String> {

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

  /**
   * Gets the {@link CallDao} used to list received calls.
   * 
   * @return the {@link CallDao} used to list received calls
   */
  public CallDao getCallDao() {
    return callDao;
  }

  /**
   * Gets the {@link ReceivedCallFactory} used to get all received call
   * information.
   * 
   * @return the {@link ReceivedCallFactory} used to get all received call
   *         information
   */
  public ReceivedCallFactory getReceivedCallFactory() {
    return receivedCallFactory;
  }

  /**
   * Gets the {@link PhoneNumber} pretty printer.
   * 
   * @return the {@link PhoneNumber} pretty printer
   */
  public Function<PhoneNumber, List<String>> getPhoneNumberPrettyPrinter() {
    return phoneNumberPrettyPrinter;
  }
}
