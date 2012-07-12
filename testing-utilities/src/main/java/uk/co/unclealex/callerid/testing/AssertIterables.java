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
 * @author unclealex72
 *
 */

package uk.co.unclealex.callerid.testing;

import static ch.lambdaj.Lambda.sort;

import java.util.List;

import org.junit.Assert;

import com.google.common.collect.Iterables;

/**
 * @author alex
 * 
 */
public class AssertIterables {

  /**
   * Check that two {@link Iterable}s are equal after being sorted.
   * @param message The message to print if the two {@link Iterable}s are not equal.
   * @param sortingLambdaExpression The lambda expression used to sort the {@link Iterable}s.
   * @param expecteds The expected values.
   * @param actuals The actual values.
   */
  public static <E> void iterablesEqual(
      String message,
      Object sortingLambdaExpression,
      Iterable<? extends E> expecteds,
      Iterable<? extends E> actuals) {
    if (expecteds == null || actuals == null) {
      Assert.assertEquals(message, expecteds, actuals);
    }
    List<? extends E> sortedExpected = sort(expecteds, sortingLambdaExpression);
    List<? extends E> sortedActual = sort(actuals, sortingLambdaExpression);
    Assert.assertArrayEquals(
        message,
        Iterables.toArray(sortedExpected, Object.class),
        Iterables.toArray(sortedActual, Object.class));
  }
}
