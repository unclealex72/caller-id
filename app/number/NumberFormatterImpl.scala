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

import javax.inject.Inject
import number.LocationConfiguration._

import scala.collection.immutable.List

/**
 * @author alex
 *
 */
class NumberFormatterImpl @Inject() (
  /**
   * The location configuration object that can be used to decide whether a number is local or not.
   */
  implicit locationConfiguration: LocationConfiguration) extends NumberFormatter {

  override def formatNumberAsInternational(phoneNumber: PhoneNumber): FormattableNumber =
    formatNumber(phoneNumber, _ => true, _ => true, _ => false)

  override def formatNumber(phoneNumber: PhoneNumber): FormattableNumber = {
    val includeInternational: Country => Boolean = country =>
      country.isNotLocal
    val includeStd: ((Country, City)) => Boolean = {
      case (country, city) => (country.isLocal && city.isNotLocal) || country.isNotLocal
    }
    formatNumber(phoneNumber, includeInternational, includeStd, _.isLocal)
  }

  //noinspection ScalaUnnecessaryParentheses
  def formatNumber(phoneNumber: PhoneNumber,
                   includeInternational: Country => Boolean,
                   includeStd: ((Country, City)) => Boolean,
                   includeLocalStdPrefix: Country => Boolean): FormattableNumber = {
    val country = phoneNumber.countries.head
    val international = Some(country).filter(includeInternational).map(_.internationalDiallingCode)
    val prefix = Some(country).filter(includeLocalStdPrefix).flatMap(_.localStdPrefix).getOrElse("")
    val city = phoneNumber.city
    val std = city.map(city => country -> city).filter(includeStd).map(_._2.stdCode)
    FormattableNumber(
      international,
      std.map(prefix + _),
      city.map(_ => "").getOrElse(prefix) + phoneNumber.number)
  }

  def formatAddress(phoneNumber: PhoneNumber): List[String] = {
    val countryName = phoneNumber.countries.head.name
    phoneNumber.city.map { city => List(city.name, countryName) }.getOrElse(List(countryName))
  }
}