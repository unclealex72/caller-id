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

package uk.co.unclealex.callerid.model;

import java.util.Date;

/**
 * A class that models a viewable received call.
 * 
 * @author alex
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
  private final String googleMapsSearchTerm;
  
  /**
   * The area to search within when using Google Maps.
   */
  private final String googleMapsSearchArea;

  /**
   * The term to use for searching on Google.
   */
  private final String googleSearchTerm;
  
  /**
   * True if this call is editable, false otherwise.
   */
  private final boolean editable;
  
  /**
   * Instantiates a new received call model.
   * 
   * @param callTime
   *          the call time
   * @param prettyPrintedPhoneNumber
   *          the pretty printed phone number
   * @param contact
   *          the contact
   * @param location
   *          the location
   * @param googleMapsSearchTerm
   *          the google search term
   * @param googleMapsSearchArea
   *          the google search area
   *          @param googleSearchTerm The google search term.
   *          @param editable True if this call is editable, false otherwise.
   */
  public ReceivedCallModel(
      Date callTime,
      String[] prettyPrintedPhoneNumber,
      String contact,
      String[] location,
      String googleMapsSearchTerm,
      String googleMapsSearchArea,
      String googleSearchTerm,
      boolean editable) {
    super();
    this.callTime = callTime;
    this.prettyPrintedPhoneNumber = prettyPrintedPhoneNumber;
    this.contact = contact;
    this.location = location;
    this.googleMapsSearchTerm = googleMapsSearchTerm;
    this.googleMapsSearchArea = googleMapsSearchArea;
    this.googleSearchTerm = googleSearchTerm;
    this.editable = editable;
  }

  /**
   * Gets the date and time the call was received.
   * 
   * @return the date and time the call was received
   */
  public Date getCallTime() {
    return callTime;
  }

  /**
   * Gets the pretty printed phone number or null if the number was withheld.
   * 
   * @return the pretty printed phone number or null if the number was withheld
   */
  public String[] getPrettyPrintedPhoneNumber() {
    return prettyPrintedPhoneNumber;
  }

  /**
   * Gets the name of the contact who made the call or none if this cannot be
   * determined.
   * 
   * @return the name of the contact who made the call or none if this cannot be
   *         determined
   */
  public String getContact() {
    return contact;
  }

  /**
   * Gets the location the call came from, in pretty printable parts.
   * 
   * @return the location the call came from, in pretty printable parts
   */
  public String[] getLocation() {
    return location;
  }

  /**
   * Gets the term to use for searching in Google Maps.
   * 
   * @return the term to use for searching in Google Maps
   */
  public String getGoogleMapsSearchTerm() {
    return googleMapsSearchTerm;
  }

  /**
   * Gets the area to search within when using Google Maps.
   * 
   * @return the area to search within when using Google Maps
   */
  public String getGoogleMapsSearchArea() {
    return googleMapsSearchArea;
  }

  /**
   * Gets the term to use for searching on Google.
   * 
   * @return the term to use for searching on Google
   */
  public String getGoogleSearchTerm() {
    return googleSearchTerm;
  }

  /**
   * Checks if is true if this call is editable, false otherwise.
   * 
   * @return the true if this call is editable, false otherwise
   */
  public boolean isEditable() {
    return editable;
  }
  
  
}
