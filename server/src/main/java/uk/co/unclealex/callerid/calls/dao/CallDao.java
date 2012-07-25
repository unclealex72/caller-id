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

package uk.co.unclealex.callerid.calls.dao;

import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.persistence.dao.BasicDao;
import uk.co.unclealex.persistence.paging.Page;

/**
 * The interface fo
 * 
 * @author alex
 * 
 */
public interface CallDao extends BasicDao<Call> {

  /**
   * Page all received calls, starting with the most recent.
   * 
   * @param pageNumber
   *          The page number to return.
   * @param pageSize
   *          The size of page to return.
   * @return A page of all received calls, starting with the most recent.
   */
  public Page<Call> pageAllByTimeReceived(long pageNumber, long pageSize);

  /**
   * Update the name of a contact of a call.
   * 
   * @param callId
   *          The id of the call to update.
   * @param newContactName
   *          The new contact name for the call.
   */
  public void updateContactName(int callId, String newContactName);

  /**
   * Get the most recent contact name for a particular phone number.
   * 
   * @param phoneNumber
   *          The phone number to search for.
   * @return The most recent contact name for the phone number (which may be
   *         null) or null if no such call has ever been received.
   */
  public String getMostRecentContactNameForPhoneNumber(String phoneNumber);
}
