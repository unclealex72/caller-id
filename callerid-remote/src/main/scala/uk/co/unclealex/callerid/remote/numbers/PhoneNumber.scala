/**
 * Copyright 2013 Alex Jones
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
package uk.co.unclealex.callerid.remote.numbers

/**
 * The representation of a telephone number that called.
 */
case class PhoneNumber(
  /**
   * The normalised number that called. A normalised number is of the form <i>+iisssnnnn</i> or <i>+iinnnn</i>
   * where <i>ii</i> is the international dialling code, <i>sss</i> is the STD code less the leading zero (if any) and <i>nnnn</i> is
   * the remaining number.
   */
  normalisedNumber: String,
  /**
   * The list of countries from where this phone number could have originated. If the city is known then this list
   * will be of length exactly one. Otherwise, the countries will be listed with the country with the most cities
   * first.
   */
  countries: Iterable[Country],
  /**
   * The city from which this phone number was made, if known.
   */
  city: Option[City],
  /**
   * The rest of the number that called, minus the international and STD codes.
   */
  number: String)