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

package uk.co.unclealex.callerid.remote.number

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import scalaz._

/**
 * @author alex
 *
 */
class NumberFormatterImplTest extends FunSuite with ShouldMatchers {

  val numberFormatter = new NumberFormatterImpl(new LocationConfiguration("44", "1256"))

  val uk = NonEmptyList(Country("United Kingdom", "44", "uk", List()))
  val basingstoke = Some(City("Basingstoke", "1256"))
  val guildford = Some(City("Guildford", "1483"))
  val france = NonEmptyList(Country("France", "33", "fr", List()))
  val paris = Some(City("Paris", "1"))

  def formatNumber(country: NonEmptyList[Country], city: Option[City], number: String) =
    numberFormatter.formatNumber(PhoneNumber("", country, city, number))

  def formatAddress(country: NonEmptyList[Country], city: Option[City], number: String) =
    numberFormatter.formatAddress(PhoneNumber("", country, city, number))

  test("International and geographic") {
    formatNumber(france, paris, "123456") should equal(List("+33", "1", "123456"))
  }

  test("International and non-geographic") {
    formatNumber(france, None, "123456789") should equal(List("+33", "123456789"))
  }

  test("National and geographic") {
    formatNumber(uk, guildford, "123456") should equal(List("01483", "123456"))
  }

  test("National and non-geographic") {
    formatNumber(uk, None, "123456789") should equal(List("0123456789"))
  }

  test("local") {
    formatNumber(uk, basingstoke, "123456") should equal(List("123456"))
  }

  test("Non-geographic address") {
    formatAddress(uk, None, "123456") should equal(List("United Kingdom"))
  }

  test("Geographic address") {
    formatAddress(uk, basingstoke, "123456") should equal(List("Basingstoke", "United Kingdom"))
  }
}