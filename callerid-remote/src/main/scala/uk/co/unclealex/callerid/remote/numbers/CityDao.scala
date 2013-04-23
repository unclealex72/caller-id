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

import java.util.Collection
import com.google.common.base.Optional

/**
 * A data access interface used to find countries and cities from telephone numbers.
 */
trait CityDao {
  /**
   * Find the international dialling code that is at the beginning of the supplied phone number.
   * @param number The phone number to scan.
   * @return The international dialling code for the country from where the phone call originated.
   */
  def extractInternationalDiallingCode(number: String): String

  /**
   * Find the city where a phone number (minus a leading zero or international dialling code) originated from.
   * @param number The phone number without an international dialling code to scan.
   * @param internationalDiallingCode The international dialling code used to limit the city search.
   * @return The city from where the phone call originated or {@link Optional#absent} if the phone number is non-geographic.
   */
  def extractCity(number: String, internationalDiallingCode: String): Option[City]

  /**
   * Get a city's country.
   * @param The city in question.
   * @return The country for the city in question.
   */
  def countryOf(city: City): Country

  /**
   * Get a list of countries that have a given international dialling code.
   * @param internationalDiallingCode The dialling code to look for.
   * @return A list of countries with the given international dialling code sorted so that the country with the
   * most cities is first.
   */
  def countries(internationalDiallingCode: String): List[Country]

}
