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

package uk.co.unclealex.callerid.areacode.dao;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.callerid.areacode.model.AreaCode;

/**
 * @author alex
 * 
 */
public class CsvAreaCodeProviderTest {

  @Test
  public void testCsv() throws IOException {
    AreaCode[] expectedAreaCodes =
        new AreaCode[] {
            areaCode("Afghanistan", "93", "Kabul", "20"),
            areaCode("Afghanistan", "93", null, null),
            areaCode("Andorra", "376", "Andorra", "7") };
    Iterable<AreaCode> actualAreaCodes = new CsvAreaCodeProvider("globalareacodes.test.csv").getObject();
    Assert.assertThat(
        "The wrong area codes were returned.",
        actualAreaCodes,
        Matchers.containsInAnyOrder(expectedAreaCodes));
  }

  protected AreaCode areaCode(String country, String countryCode, String areaName, String areaCode) {
    AreaCode ac = new AreaCode();
    ac.setCountry(country);
    ac.setCountryCode(countryCode);
    ac.setArea(areaName);
    ac.setAreaCode(areaCode);
    return ac;
  }
}
