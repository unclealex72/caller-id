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


/**
 * @author alex
 *
 */
public class PageModel {

  /**
   * The offset for this page.
   */
  private final int firstIndex;
  
  private final int lastIndex;
  
  private final int totalResultCount;
  
  /**
   * The number of the previous page or null if this is the first page.
   */
  private final Integer previousPage;
  
  /**
   * The number of the next page or null if this is the last page.
   */
  private final Integer nextPage;
  
  /**
   * The current page number.
   */
  private final int currentPage;

  private final int lastPage;

  private final List<Integer> allPages;

  /**
   * @param firstIndex
   * @param lastIndex
   * @param totalResultCount
   * @param previousPage
   * @param nextPage
   * @param currentPage
   * @param lastPage
   * @param allPages
   */
  public PageModel(
      int firstIndex,
      int lastIndex,
      int totalResultCount,
      Integer previousPage,
      Integer nextPage,
      int currentPage,
      int lastPage,
      List<Integer> allPages) {
    super();
    this.firstIndex = firstIndex;
    this.lastIndex = lastIndex;
    this.totalResultCount = totalResultCount;
    this.previousPage = previousPage;
    this.nextPage = nextPage;
    this.currentPage = currentPage;
    this.lastPage = lastPage;
    this.allPages = allPages;
  }

  public int getFirstIndex() {
    return firstIndex;
  }

  public int getLastIndex() {
    return lastIndex;
  }

  public int getTotalResultCount() {
    return totalResultCount;
  }

  public Integer getPreviousPage() {
    return previousPage;
  }

  public Integer getNextPage() {
    return nextPage;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getLastPage() {
    return lastPage;
  }

  public List<Integer> getAllPages() {
    return allPages;
  }
  
 
}