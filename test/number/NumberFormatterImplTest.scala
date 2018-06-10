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

import cats.data._
import org.scalatest._

import scala.collection.SortedSet

/**
 * @author alex
 *
 */
class NumberFormatterImplTest extends WordSpec with Matchers {

  val localService = new LocalServiceImpl(internationalCode = "44", stdCode = "1256")
  val numberFormatter = new NumberFormatterImpl(localService)

  val uk = NonEmptyList.of(Country("United Kingdom", "44", "uk", Some("0"), SortedSet.empty))
  val basingstoke = Some(City("Basingstoke", "1256"))
  val guildford = Some(City("Guildford", "1483"))
  val france = NonEmptyList.of(Country("France", "33", "fr", None, SortedSet.empty))
  val paris = Some(City("Paris", "1"))

  def formatNumber(country: NonEmptyList[Country], city: Option[City], number: String): String =
    numberFormatter.formatNumber(country, city, number)



  "International geographic numbers" should {
    "always be formatted with a country code and a city code" in {
      formatNumber(france, paris, "123456") should ===("+33 (1) 123456")
    }
  }

  "International non-geographic numbers" should {
    "always be formatted with a country code but no city code" in {
      formatNumber(france, None, "123456789") should ===("+33 123456789")
    }
  }

  "National geographic numbers" should {
    "be locally formatted without a country code but with a city code" in {
      formatNumber(uk, guildford, "123456") should ===("(01483) 123456")
    }
  }

  "National non-geographic numbers" should {
    "be locally formatted without a country code and without a city code" in {
      formatNumber(uk, None, "123456789") should ===("0123456789")
    }
  }

  "Local numbers" should {
    "be locally formatted as only a number" in {
      formatNumber(uk, basingstoke, "123456") should ===("123456")
    }
  }
}