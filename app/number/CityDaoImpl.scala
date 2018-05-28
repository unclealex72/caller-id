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

import cats.data.NonEmptyList
import play.api.libs.json.{JsError, JsSuccess, Json}

/**
 * An implementation of [[CityDao]] that uses a JSON resource
 * to store countries and cities.
 */
class CityDaoImpl(val countries: List[Country]) extends CityDao {

  /**
   * A multimap of all countries keyed by their international dialling codes. The collection values of this map are ordered
   * such that countries with more cities are listed first.
   */
  val countriesByInternationalDiallingCode: Map[String, Seq[Country]] =
    countries.groupBy(_.internationalDiallingCode).mapValues(_.sortBy(c => (-c.cities.size, c.name)))

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

  override def extractInternationalDiallingCode(number: String): Option[String] =
    internationalDiallingCodes.find(number.startsWith)

  override def extractCity(number: String, internationalDiallingCode: String): Option[City] = {
    val cities = citiesByInternationalDiallingCode.getOrElse(internationalDiallingCode, Seq.empty)
    cities.find((c: City) => number.startsWith(c.stdCode))
  }

  override def countryOf(city: City): Option[Country] = {
    countriesByCity.get(city)
  }

  override def countries(internationalDiallingCode: String): Option[NonEmptyList[Country]] = {
    countriesByInternationalDiallingCode.getOrElse(internationalDiallingCode, Seq.empty).toList match {
      case c :: cc => Some(NonEmptyList(c, cc))
      case Nil => None
    }
  }

  override def all(): Seq[Country] = countries
}

object CityDaoImpl {

  def apply(): CityDaoImpl = {
    val fileName = "countries.json"
    val source = Option(classOf[CityDaoImpl].getClassLoader.getResourceAsStream("countries.json")).getOrElse(
      throw new FileNotFoundException(fileName)
    )
    val countryList = Json.parse(source).validate[Countries] match {
      case JsSuccess(countries, _) => countries.countries
      case JsError(errors) =>
        val errorReports = for {
          (path, validationErrors) <- errors
          validationError <- validationErrors
        } yield {
          s"${path.toJsonString} ${validationError.message}"
        }
        throw new IllegalStateException(errorReports.mkString("\n"))
    }
    new CityDaoImpl(countryList)
  }
}