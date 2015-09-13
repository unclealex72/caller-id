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
package number

import java.io.FileNotFoundException

import argonaut.Argonaut._
import argonaut._
import com.typesafe.scalalogging.StrictLogging

import scala.io.{Codec, Source}
import scalaz.NonEmptyListFunctions

/**
 * An implementation of {@link CityDao} that uses a JSON resource
 * to store countries and cities.
 */
class CityDaoImpl(val countries: List[Country]) extends CityDao with NonEmptyListFunctions {

  /**
   * A multimap of all countries keyed by their international dialling codes. The collection values of this map are ordered
   * such that countries with more cities are listed first.
   */
  val countriesByInternationalDiallingCode: Map[String, Seq[Country]] =
    countries.groupBy(_.internationalDiallingCode).mapValues(_.sortBy(c => (-c.cities.length, c.name)))

  /**
   * A multimap of all cities keyed by their international dialling codes. The collection values of this map are ordered
   * such that cities with longer std codes are listed first.
   */
  val citiesByInternationalDiallingCode: Map[String, Seq[City]] =
    countriesByInternationalDiallingCode.mapValues(_.flatMap(_.cities).sortBy(c => (-c.stdCode.length, c.stdCode, c.name)))

  /**
   * A set of all known international dialling codes. The dialling codes are ordered longest first and then by value.
   */
  val internationalDiallingCodes: Seq[String] = countries.map(_.internationalDiallingCode).sortBy((s: String) => (-s.length, s))

  /**
   * A map that holds the country for each city.
   */
  val countriesByCity: Map[City, Country] = countries.foldLeft(Map.empty[City, Country]) { case (map, country) =>
    map ++ country.cities.map(_ -> country)
  }

  override def extractInternationalDiallingCode(number: String) =
    internationalDiallingCodes.find(number.startsWith)

  override def extractCity(number: String, internationalDiallingCode: String) = {
    val cities = citiesByInternationalDiallingCode.getOrElse(internationalDiallingCode, Seq.empty)
    cities.find((c: City) => number.startsWith(c.stdCode))
  }

  override def countryOf(city: City) = {
    countriesByCity.get(city)
  }

  override def countries(internationalDiallingCode: String) = {
    countriesByInternationalDiallingCode.getOrElse(internationalDiallingCode, Seq.empty).toList match {
      case c :: cc => Some(nel(c, cc))
      case Nil => None
    }
  }
}

case class Countries(countries: List[Country])
object Countries extends StrictLogging {

  def apply(): Countries = parseJson("countries.json")
  def parseJson(resourceName: String): Countries = {
    Option(getClass.getClassLoader.getResource(resourceName)) match {
      case Some(url) =>
        val source = Source.fromURL(url)(Codec.UTF8)
        logger info s"Loading countries information from $url"
        try {
          Parse.decodeValidation[Countries](source.mkString).toValidationNel.valueOr { errors =>
            throw new IllegalStateException("Cannot parse STD code information:\n" + errors.stream.mkString("\n"))
          }
        } finally {
          source.close
        }
      case None => throw new FileNotFoundException("Cannot find resource countries.json")
    }

  }
  implicit def CountriesCodec: CodecJson[Countries] = casecodec1(Countries.apply, Countries.unapply)("countries")
}