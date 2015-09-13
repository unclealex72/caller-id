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

import util.OptionToValidation._

import scalaz.{NonEmptyList, ValidationNel}
import scalaz.Validation.FlatMap._

/**
 * The default implementation of {@link NumberLocationService}.
 */
class NumberLocationServiceImpl(
                                 /**
                                  * The {@link CityDao} used to find cities and countries from telephone numbers.
                                  */
                                 cityDao: CityDao,

                                 /**
                                  * The {@link LocationConfiguration} of the telephone number that is receiving calls.
                                  */
                                 locationConfiguration: LocationConfiguration) extends NumberLocationService {

  override def decompose(number: String): ValidationNel[String, PhoneNumber] = {
    val trimmedNumber = number.replaceAll("\\s+", "")
    val functionsByPrefix =
      List(
        PrefixAndParser("00", international),
        PrefixAndParser("+", international),
        PrefixAndParser("0", national),
        PrefixAndParser("", local))
    val matchingPrefixAndParser = functionsByPrefix.find { pp => trimmedNumber.startsWith(pp.prefix) }.get
    val actualNumber = matchingPrefixAndParser.parser(
      trimmedNumber.substring(matchingPrefixAndParser.prefix.length))
    toPhoneNumber(actualNumber)
  }

  /**
   * Convert an international string phone number into a normalised phone number.
   * @return A function that converts an international string phone number into a normalised phone number.
   */
  def international = (number: String) => number

  /**
   * Convert a national string phone number into a normalised phone number.
   * @return A function that converts a national string phone number into a normalised phone number.
   */
  def national = (number: String) => locationConfiguration.internationalCode + number

  /**
   * Convert a legacy.local string phone number into a normalised phone number.
   * @return A function that converts a legacy.local string phone number into a normalised phone number.
   */
  def local = (number: String) => locationConfiguration.internationalCode + locationConfiguration.stdCode + number

  /**
   * Convert a phone number containing the international dialling code (without a 00 or + prefix),
   * the std code (without the 0 prefix)
   * and the rest of the number into a normalised phone number.
   * @param The number to convert.
   * @return A normalised phone number.
   */
  def toPhoneNumber(number: String): ValidationNel[String, PhoneNumber] = {
    val normalisedNumber = "+" + number
    val idcValidation = cityDao.extractInternationalDiallingCode(number) ~~ s"Cannot find a international dialling code for $number"
    idcValidation.flatMap { idc =>
      val nationalNumber = number.substring(idc.length)
      cityDao.extractCity(nationalNumber, idc) match {
        case Some(city) =>
          val countryValidation = cityDao.countryOf(city) ~~ s"Cannot find a country for city ${city.name}"
          countryValidation.map { country =>
            PhoneNumber(normalisedNumber, NonEmptyList(country), Some(city), nationalNumber.substring(city.stdCode.length()))
          }
        case None =>
          val countriesValidation = cityDao.countries(idc) ~~ s"Cannot find any countries with international dialling code $idc"
          countriesValidation.map { countries =>
            PhoneNumber(normalisedNumber, countries, None, nationalNumber)
          }
      }
    }
  }

  case class PrefixAndParser(prefix: String, parser: String => String)

}
