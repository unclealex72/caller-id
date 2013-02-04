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

package uk.co.unclealex.callerid.areacode.model

import org.eclipse.xtend.lib.Data

/**
 * A class that encapsulates all of the information about telephone area codes
 * and the areas they represent.
 * 
 * @author alex
 * 
 */
@Data
public class AreaCode {

  /**
   * The country of origin for this area code.
   */
  Country country;

  /**
   * The town or city this area code represents.
   */
  String area;

  /**
   * The area code itself. This field can contain spurious characters at the
   * beginning so only digits from the end of the string should be used.
   */
  String areaCode;

}
