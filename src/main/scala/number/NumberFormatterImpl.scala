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
 * @author unclealex72
 *
 */

package number

import scala.collection.immutable.List

/**
 * @author alex
 *
 */
class NumberFormatterImpl(
  /**
   * The location configuration object that can be used to decide whether a number is legacy.local or not.
   */
  locationConfiguration: LocationConfiguration) extends NumberFormatter {

  def formatNumberAsInternational(phoneNumber: PhoneNumber): List[String] = {
    List(
      Some(s"+${phoneNumber.countries.head.internationalDiallingCode}"),
      phoneNumber.city.map(_.stdCode),
      Some(phoneNumber.number)).filter(_.isDefined).map(_.get)
  }

  def formatNumber(phoneNumber: PhoneNumber): List[String] = {
    val country = phoneNumber.countries.head
    val internationalDiallingCode =
      if (country.internationalDiallingCode != locationConfiguration.internationalCode)
        Some(country.internationalDiallingCode) else None
    internationalDiallingCode.map { internationalDiallingCode =>
      List(Some(s"+$internationalDiallingCode"), phoneNumber.city.map(_.stdCode), Some(phoneNumber.number))
    }.getOrElse {
      phoneNumber.city.map { city =>
        List(if (city.stdCode == locationConfiguration.stdCode) None else Some(s"0${city.stdCode}"), Some(phoneNumber.number))
      }.getOrElse {
        List(Some(s"0${phoneNumber.number}"))
      }
    }.filter(_.isDefined).map(_.get)
  }

  def formatAddress(phoneNumber: PhoneNumber): List[String] = {
    val countryName = phoneNumber.countries.head.name
    phoneNumber.city.map { city => List(city.name, countryName) }.getOrElse(List(countryName))
  }
}