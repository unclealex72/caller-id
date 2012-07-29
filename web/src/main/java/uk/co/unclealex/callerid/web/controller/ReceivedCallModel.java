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

import java.util.Date;

/**
 * @author alex
 *
 */
public class ReceivedCallModel {

  /**
   * The date and time the call was received.
   */
  private final Date callTime;
  
  /**
   * The pretty printed phone number or null if the number was withheld.
   */
  private final String[] prettyPrintedPhoneNumber;
  
  /**
   * The name of the contact who made the call or none if this cannot be determined.
   */
  private final String contact;

  /**
   * The location the call came from, in pretty printable parts.
   */
  private final String[] location;
  
  /**
   * The term to use for searching in Google Maps.
   */
  private final String googleSearchTerm;
  
  /**
   * The area to search within when using Google Maps.
   */
  private final String googleSearchArea;

  /**
   * @param callTime
   * @param prettyPrintedPhoneNumber
   * @param contact
   * @param location
   * @param googleSearchTerm
   * @param googleSearchArea
   */
  public ReceivedCallModel(
      Date callTime,
      String[] prettyPrintedPhoneNumber,
      String contact,
      String[] location,
      String googleSearchTerm,
      String googleSearchArea) {
    super();
    this.callTime = callTime;
    this.prettyPrintedPhoneNumber = prettyPrintedPhoneNumber;
    this.contact = contact;
    this.location = location;
    this.googleSearchTerm = googleSearchTerm;
    this.googleSearchArea = googleSearchArea;
  }

  public Date getCallTime() {
    return callTime;
  }

  public String[] getPrettyPrintedPhoneNumber() {
    return prettyPrintedPhoneNumber;
  }

  public String getContact() {
    return contact;
  }

  public String[] getLocation() {
    return location;
  }

  public String getGoogleSearchTerm() {
    return googleSearchTerm;
  }

  public String getGoogleSearchArea() {
    return googleSearchArea;
  }
  
  
}
