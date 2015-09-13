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

import org.specs2.mutable.Specification

import scalaz._

/**
 * @author alex
 *
 */
class NumberFormatterImplTest extends Specification {

  val numberFormatter = new NumberFormatterImpl(new LocationConfiguration("44", "1256"))

  val uk = NonEmptyList(Country("United Kingdom", "44", "uk", List()))
  val basingstoke = Some(City("Basingstoke", "1256"))
  val guildford = Some(City("Guildford", "1483"))
  val france = NonEmptyList(Country("France", "33", "fr", List()))
  val paris = Some(City("Paris", "1"))

  def formatNumber(country: NonEmptyList[Country], city: Option[City], number: String) =
    numberFormatter.formatNumber(PhoneNumber("", country, city, number))

  def formatNumberAsInternational(country: NonEmptyList[Country], city: Option[City], number: String) =
    numberFormatter.formatNumberAsInternational(PhoneNumber("", country, city, number))

  def formatAddress(country: NonEmptyList[Country], city: Option[City], number: String) =
    numberFormatter.formatAddress(PhoneNumber("", country, city, number))

  "International geographic numbers" should {
    "always be formatted with a country code and a city code" in {
      formatNumber(france, paris, "123456") must beEqualTo(List("+33", "1", "123456"))
      formatNumberAsInternational(france, paris, "123456") must beEqualTo(List("+33", "1", "123456"))

    }
  }

  "International non-geographic numbers" should {
    "always be formatted with a country code but no city code" in {
      formatNumber(france, None, "123456789") must beEqualTo(List("+33", "123456789"))
      formatNumberAsInternational(france, None, "123456789") must beEqualTo(List("+33", "123456789"))

    }
  }

  "National geographic numbers" should {
    "be locally formatted without a country code but with a city code" in {
      formatNumber(uk, guildford, "123456") must beEqualTo(List("01483", "123456"))
    }
    "be internationally formatted with a country code and a city code" in {
      formatNumberAsInternational(uk, guildford, "123456") must beEqualTo(List("+44", "1483", "123456"))
    }
  }

  "National non-geographic numbers" should {
    "be locally formatted without a country code and without a city code" in {
      formatNumber(uk, None, "123456789") must beEqualTo(List("0123456789"))
    }
    "be internationally formatted with a country code and without a city code" in {
      formatNumberAsInternational(uk, None, "123456789") must beEqualTo(List("+44", "123456789"))
    }
  }

  "Local numbers" should {
    "be locally formatted as only a number" in {
      formatNumber(uk, basingstoke, "123456") must beEqualTo(List("123456"))
    }
    "be internationally formatted with a country code and city code" in {
      formatNumberAsInternational(uk, basingstoke, "123456") must beEqualTo(List("+44", "1256", "123456"))

    }
  }

  "A non-geographic address" should {
    "be formatted as only its country" in {
      formatAddress(uk, None, "123456") must beEqualTo(List("United Kingdom"))
    }
  }

  "A geographic address" should {
    "be fully formatted into a city and a country" in {
      formatAddress(uk, basingstoke, "123456") must beEqualTo(List("Basingstoke", "United Kingdom"))
    }
  }
}