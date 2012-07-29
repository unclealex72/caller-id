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
 * The model class for pages.
 * 
 * @author alex
 */
public class PageModel {

  /**
   * The index of the first element on this page.
   */
  private final int firstIndex;
  
  /**
   * The index of the last element on this page.
   */
  private final int lastIndex;
  
  /**
   * The total number of results.
   */
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

  /**
   * The last page number.
   */
  private final int lastPage;

  /**
   * A list of all the available page numbers.
   */
  private final List<Integer> allPages;

  /**
   * Instantiates a new page model.
   * 
   * @param firstIndex
   *          the first index
   * @param lastIndex
   *          the last index
   * @param totalResultCount
   *          the total result count
   * @param previousPage
   *          the previous page
   * @param nextPage
   *          the next page
   * @param currentPage
   *          the current page
   * @param lastPage
   *          the last page
   * @param allPages
   *          the all pages
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

  /**
   * Gets the index of the first element on this page.
   * 
   * @return the index of the first element on this page
   */
  public int getFirstIndex() {
    return firstIndex;
  }

  /**
   * Gets the index of the last element on this page.
   * 
   * @return the index of the last element on this page
   */
  public int getLastIndex() {
    return lastIndex;
  }

  /**
   * Gets the total number of results.
   * 
   * @return the total number of results
   */
  public int getTotalResultCount() {
    return totalResultCount;
  }

  /**
   * Gets the number of the previous page or null if this is the first page.
   * 
   * @return the number of the previous page or null if this is the first page
   */
  public Integer getPreviousPage() {
    return previousPage;
  }

  /**
   * Gets the number of the next page or null if this is the last page.
   * 
   * @return the number of the next page or null if this is the last page
   */
  public Integer getNextPage() {
    return nextPage;
  }

  /**
   * Gets the current page number.
   * 
   * @return the current page number
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * Gets the last page number.
   * 
   * @return the last page number
   */
  public int getLastPage() {
    return lastPage;
  }

  /**
   * Gets the a list of all the available page numbers.
   * 
   * @return the a list of all the available page numbers
   */
  public List<Integer> getAllPages() {
    return allPages;
  }
  
 
}