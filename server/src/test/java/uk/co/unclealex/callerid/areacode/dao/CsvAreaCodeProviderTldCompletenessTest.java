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
import java.util.List;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.callerid.areacode.model.AreaCode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class CsvAreaCodeProviderTldCompletenessTest {

  @Test
  public void testTldCompleteness() throws IOException {
    List<AreaCode> areaCodes = new CsvAreaCodeFactory("globalareacodes.csv", "tlds.csv").createAreaCodes();
    Predicate<AreaCode> missingTldCodePredicate = new Predicate<AreaCode>() {
      @Override
      public boolean apply(AreaCode areaCode) {
        return areaCode.getCountry().getTld() == null;
      }
    };
    Function<AreaCode, String> countryFunction = new Function<AreaCode, String>() {
      public String apply(AreaCode areaCode) {
        return areaCode.getCountry().getName();
      }
    };
    SortedSet<String> missingTlds =
        Sets.newTreeSet(Iterables.transform(Iterables.filter(areaCodes, missingTldCodePredicate), countryFunction));
    Assert.assertEquals("The following countries did not have tlds.", "", Joiner.on(", ").join(missingTlds));
  }
}
