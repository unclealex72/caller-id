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

package uk.co.unclealex.callerid;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.calls.dao.CallDao;
import uk.co.unclealex.callerid.calls.model.Call;
import au.com.bytecode.opencsv.CSVReader;

/**
 * @author alex
 *
 */
@Transactional
public class CallsLoader implements Serializable {

  private final CallDao callDao;

  @PostConstruct
  public void load() throws ParseException, IOException {
    //2011-11-08 20:09:59
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try (CSVReader reader = new CSVReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("calls.txt")))) {
      for (String[] fields : reader.readAll()) {
        Date date = df.parse(fields[0]);
        String number = fields[1];
        Call call = new Call(date, number, null);
        getCallDao().store(call);
      }
    }
  }
  /**
   * @param callDao
   */
  @Inject
  public CallsLoader(CallDao callDao) {
    super();
    this.callDao = callDao;
  }

  public CallDao getCallDao() {
    return callDao;
  }
  
  
}
