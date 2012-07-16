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

import uk.co.unclealex.callerid.calls.dao.CallDao;
import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.persistence.paging.Page;

/**
 * @author alex
 *
 */
public class DummyCallDao implements CallDao {

  /**
   * {@inheritDoc}
   */
  @Override
  public Call store(Call entity) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete(Call entity) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Call> getAll() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<Call> pageAllByTimeReceived(long pageNumber, long pageSize) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateContactName(int callId, String newContactName) {
  }

}
