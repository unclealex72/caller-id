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
 * http://www.apache.org/licenses/LICENSE-2.0
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
package number

import cats.data._
import cats.implicits._

/**
 * The default implementation of [[PhoneNumberFactory]].
 */
class PhoneNumberFactoryImpl(
                                 /**
                                  * The [[CityDao]] used to find cities and countries from telephone numbers.
                                  */
                                 cityDao: CityDao,
                                 /**
                                   * The [[NumberFormatter]] used to pretty print a phone number.
                                   */
                                 numberFormatter: NumberFormatter,
                                 /**
                                  * The [[LocationConfiguration]] of the telephone number that is receiving calls.
                                  */
                                 localService: LocalService) extends PhoneNumberFactory {

  override def apply(number: String): ValidatedNel[String, PhoneNumber] = {
    val trimmedNumber: String = number.replaceAll("\\s+", "")
    val functionsByPrefix =
      List(
        PrefixAndParser("00", international),
        PrefixAndParser("+", international),
        PrefixAndParser("0", national),
        PrefixAndParser("", local))
    val matchingPrefixAndParser: PrefixAndParser = functionsByPrefix.find { pp => trimmedNumber.startsWith(pp.prefix) }.get
    val actualNumber = matchingPrefixAndParser.parser(
      trimmedNumber.substring(matchingPrefixAndParser.prefix.length))
    toPhoneNumber(actualNumber).toValidatedNel
  }

  /**
   * Convert an international string phone number into a normalised phone number.
   * @return A function that converts an international string phone number into a normalised phone number.
   */
  val international: String => String = (number: String) => number

  /**
   * Convert a national string phone number into a normalised phone number.
   * @return A function that converts a national string phone number into a normalised phone number.
   */
  val national: String => String = (number: String) => localService.internationalCode + number

  /**
   * Convert a legacy.local string phone number into a normalised phone number.
   * @return A function that converts a legacy.local string phone number into a normalised phone number.
   */
  val local: String => String = (number: String) => localService.internationalCode + localService.stdCode + number

  /**
   * Convert a phone number containing the international dialling code (without a 00 or + prefix),
   * the std code (without the 0 prefix)
   * and the rest of the number into a normalised phone number.
   * @param number The number to convert.
   * @return A normalised phone number.
   */
  def toPhoneNumber(number: String): Either[String, PhoneNumber] = {
    val normalisedNumber: String = "+" + number
    val idcValidation: Either[String, String] =
      cityDao.extractInternationalDiallingCode(number).toRight(s"Cannot find a international dialling code for $number")
    idcValidation.flatMap { idc =>
      val nationalNumber: String = number.substring(idc.length)
      cityDao.extractCity(nationalNumber, idc) match {
        case Some(city) =>
          val countryValidation: Either[String, Country] = cityDao.countryOf(city).toRight(s"Cannot find a country for city ${city.name}")
          countryValidation.map { country =>
            val localNumber: String = nationalNumber.substring(city.stdCode.length())
            val formattedNumber: String = numberFormatter.formatNumber(NonEmptyList.of(country), Some(city), localNumber)
            PhoneNumber(normalisedNumber, formattedNumber, Some(city.name), NonEmptyList.of(country.name))
          }
        case None =>
          val countriesValidation: Either[String, NonEmptyList[Country]] = cityDao.countries(idc).toRight(s"Cannot find any countries with international dialling code $idc")
          countriesValidation.map { countries =>
            val formattedNumber: String = numberFormatter.formatNumber(countries, None, nationalNumber)
            PhoneNumber(normalisedNumber, formattedNumber, None, countries.map(_.name))
          }
      }
    }
  }

  case class PrefixAndParser(prefix: String, parser: String => String)

}
