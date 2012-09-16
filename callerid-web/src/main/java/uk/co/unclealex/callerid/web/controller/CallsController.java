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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import uk.co.unclealex.callerid.calls.dao.CallDao;
import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.model.ReceivedCallModel;

import com.google.common.base.Function;
import com.google.common.base.Functions;
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
   * The default page size.
   */
  static final int DEFAULT_PAGE_SIZE = 7;

  /**
   * The number of calls on each page.
   */
  private final int pageSize;

  /**
   * The {@link CallDao} used to list received calls.
   */
  private final CallDao callDao;

  /**
   * A {@link Function} used to get all received call information.
   */
  private final Function<Call, ReceivedCall> receivedCallFunction;

  /**
   * A {@link Function} used to get all received call model information.
   */
  private final Function<ReceivedCall, ReceivedCallModel> receivedCallModelFunction;

  @Inject
  public CallsController(
      CallDao callDao,
      @Named("receivedCallFunction") Function<Call, ReceivedCall> receivedCallFunction,
      @Named("receivedCallModelFunction") Function<ReceivedCall, ReceivedCallModel> receivedCallModelFunction) {
    this(DEFAULT_PAGE_SIZE, callDao, receivedCallFunction, receivedCallModelFunction);
  }

  public CallsController(
      int pageSize,
      CallDao callDao,
      Function<Call, ReceivedCall> receivedCallFunction,
      Function<ReceivedCall, ReceivedCallModel> receivedCallModelFunction) {
    super();
    this.pageSize = pageSize;
    this.callDao = callDao;
    this.receivedCallFunction = receivedCallFunction;
    this.receivedCallModelFunction = receivedCallModelFunction;
  }

  /**
   * Get all calls and store each page of calls in a map keyed by the one-based
   * page number.
   * 
   * @return A model and view for listing each page of received calls.
   */
  @RequestMapping(value = "/calls.html", method = RequestMethod.GET)
  public ModelAndView listCalls() {
    Function<Call, ReceivedCallModel> receivedCallModelFunction =
        Functions.compose(getReceivedCallModelFunction(), getReceivedCallFunction());
    Iterable<List<ReceivedCallModel>> receivedCalls =
        Iterables.partition(
            Iterables.transform(getCallDao().getAllByTimeReceived(), receivedCallModelFunction),
            getPageSize());
    ModelAndView mav = new ModelAndView("calls");
    mav.getModel().put("callPages", Lists.newArrayList(receivedCalls));
    return mav;
  }

  /**
   * Gets the {@link CallDao} used to list received calls.
   * 
   * @return the {@link CallDao} used to list received calls
   */
  public CallDao getCallDao() {
    return callDao;
  }

  public int getPageSize() {
    return pageSize;
  }

  public Function<Call, ReceivedCall> getReceivedCallFunction() {
    return receivedCallFunction;
  }

  public Function<ReceivedCall, ReceivedCallModel> getReceivedCallModelFunction() {
    return receivedCallModelFunction;
  }
}
