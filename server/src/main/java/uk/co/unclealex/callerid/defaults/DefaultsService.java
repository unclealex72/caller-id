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

package uk.co.unclealex.callerid.defaults;

/**
 * An interface used to return any default values.
 * @author alex
 *
 */
public interface DefaultsService {

  /**
   * Get the country code for the phone number that is receiving calls.
   * @return The country code for the phone number that is receiving calls.
   */
  public String getCountryCode();
  
  /**
   * Get the area code for the phone number that is receiving calls.
   * @return The area code for the phone number that is receiving calls.
   */
  public String getAreaCode();
}
