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

package uk.co.unclealex.callerid.squeezebox;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import uk.co.unclealex.callerid.calls.model.ReceivedCall;
import uk.co.unclealex.callerid.google.model.Contact;
import uk.co.unclealex.callerid.modem.listener.CallListener;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberLocationVisitor;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberPrettyPrinterVisitor;
import uk.co.unclealex.callerid.phonenumber.PhoneNumberWithheldVisitor;
import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;

import com.google.common.base.Joiner;

/**
 * The listener interface for receiving squeezeboxCall events. The class that is
 * interested in processing a squeezeboxCall event implements this interface,
 * and the object created with that class is registered with a component using
 * the component's <code>addSqueezeboxCallListener<code> method. When
 * the squeezeboxCall event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SqueezeboxCallEvent
 */
public class SqueezeboxCallListener implements CallListener {

  /**
   * The previously calculated message to display.
   */
  private String messageToDisplay;

  /**
   * The {@link SqueezeboxCliFactory} used to get {@link SqueezeboxCli}
   * instances.
   */
  private final SqueezeboxCliFactory squeezeboxCliFactory;

  /**
   * The {@link PhoneNumberPrettyPrinterVisitor} used to pretty print.
   * {@link PhoneNumber}s.
   */
  private final PhoneNumberPrettyPrinterVisitor phoneNumberPrettyPrinterVisitor;

  /**
   * The {@link PhoneNumberLocationVisitor} used to pretty print.
   * {@link PhoneNumber} locations.
   */
  private final PhoneNumberLocationVisitor phoneNumberLocationVisitor;

  /**
   * The {@link PhoneNumberWithheldVisitor} used to decide whether a.
   * {@link PhoneNumber} has been withheld or not.
   */
  private final PhoneNumberWithheldVisitor phoneNumberWithheldVisitor;

  /**
   * @param squeezeboxFactory
   * @param phoneNumberPrettyPrinterVisitor
   * @param phoneNumberLocationVisitor
   * @param phoneNumberWithheldVisitor
   */
  @Inject
  public SqueezeboxCallListener(
      SqueezeboxCliFactory squeezeboxCliFactory,
      PhoneNumberPrettyPrinterVisitor phoneNumberPrettyPrinterVisitor,
      PhoneNumberLocationVisitor phoneNumberLocationVisitor,
      PhoneNumberWithheldVisitor phoneNumberWithheldVisitor) {
    super();
    this.squeezeboxCliFactory = squeezeboxCliFactory;
    this.phoneNumberPrettyPrinterVisitor = phoneNumberPrettyPrinterVisitor;
    this.phoneNumberLocationVisitor = phoneNumberLocationVisitor;
    this.phoneNumberWithheldVisitor = phoneNumberWithheldVisitor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean onNumber(ReceivedCall receivedCall) {
    PhoneNumber phoneNumber = receivedCall.getPhoneNumber();
    String message;
    if (phoneNumber.accept(getPhoneNumberWithheldVisitor())) {
      message = "Withheld";
    }
    else {
      String contactName = receivedCall.getContactName();
      if (contactName != null) {
        message = contactName;
      }
      else {
        List<Contact> contacts = receivedCall.getContacts();
        if (!contacts.isEmpty()) {
          message = contacts.get(0).getName();
        }
        else {
          String telephoneNumber = Joiner.on(' ').join(phoneNumber.accept(getPhoneNumberPrettyPrinterVisitor()));
          String location = Joiner.on(", ").join(phoneNumber.accept(getPhoneNumberLocationVisitor()));
          message = String.format("%s (%s)", telephoneNumber, location);
        }
      }
    }
    setMessageToDisplay(message);
    return true;
  }

  /**
   * Print the message that was created during the.
   * 
   * @return true, if successful
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   *           {@link #onNumber(ReceivedCall)} method.
   */
  @Override
  public boolean onRing() throws IOException {
    try (SqueezeboxCli squeezeboxCli = getSqueezeboxCliFactory().create()) {
      int playerCount = squeezeboxCli.countPlayers();
      for (int idx = 0; idx < playerCount; idx++) {
        squeezeboxCli.display(idx, "Incoming call", getMessageToDisplay(), 30);
      }
    }
    return true;
  }

  /**
   * Gets the {@link PhoneNumberPrettyPrinterVisitor} used to pretty print.
   * 
   * @return the {@link PhoneNumberPrettyPrinterVisitor} used to pretty print
   */
  public PhoneNumberPrettyPrinterVisitor getPhoneNumberPrettyPrinterVisitor() {
    return phoneNumberPrettyPrinterVisitor;
  }

  /**
   * Gets the {@link PhoneNumberLocationVisitor} used to pretty print.
   * 
   * @return the {@link PhoneNumberLocationVisitor} used to pretty print
   */
  public PhoneNumberLocationVisitor getPhoneNumberLocationVisitor() {
    return phoneNumberLocationVisitor;
  }

  /**
   * Gets the {@link PhoneNumberWithheldVisitor} used to decide whether a.
   * 
   * @return the {@link PhoneNumberWithheldVisitor} used to decide whether a
   */
  public PhoneNumberWithheldVisitor getPhoneNumberWithheldVisitor() {
    return phoneNumberWithheldVisitor;
  }

  /**
   * Gets the previously calculated message to display.
   * 
   * @return the previously calculated message to display
   */
  public String getMessageToDisplay() {
    return messageToDisplay;
  }

  /**
   * Sets the previously calculated message to display.
   * 
   * @param messageToDisplay
   *          the new previously calculated message to display
   */
  public void setMessageToDisplay(String messageToDisplay) {
    this.messageToDisplay = messageToDisplay;
  }

  public SqueezeboxCliFactory getSqueezeboxCliFactory() {
    return squeezeboxCliFactory;
  }
}