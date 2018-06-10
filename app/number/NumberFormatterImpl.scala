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

import cats.data.NonEmptyList
import javax.inject.Inject

/**
 * @author alex
 *
 */
class NumberFormatterImpl @Inject() (
  /**
   * The local service object that can be used to decide whether a number is local or not.
   */
  localService: LocalService) extends NumberFormatter {

  def formatNumber(countries: NonEmptyList[Country], maybeCity: Option[City], number: String): String = {
    val country: Country = countries.head
    val countryCode: Seq[String] =
      Seq(country).filterNot(localService.isLocalCountry).map(c => s"+${c.internationalDiallingCode}")
    val internalPrefix = country.localStdPrefix.getOrElse("")
    val nationalParts: Seq[String] = maybeCity match {
      case Some(city) =>
        if (localService.isLocalCity(city)) {
          Seq(number)
        }
        else {
          Seq(s"($internalPrefix${city.stdCode})", number)
        }
      case None => Seq(internalPrefix + number)
    }
    (countryCode ++ nationalParts).mkString(" ")
  }


}