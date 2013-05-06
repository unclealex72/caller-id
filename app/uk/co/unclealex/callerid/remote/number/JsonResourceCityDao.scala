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
package uk.co.unclealex.callerid.remote.number

import scala.collection.mutable.HashMap
import scala.math.Ordering
import scala.math.Ordering.Implicits._
import scala.collection.mutable.TreeSet
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import javax.annotation.PostConstruct
import com.fasterxml.jackson.core.`type`.TypeReference
import scala.collection.mutable.HashSet
import scalaz.NonEmptyList
import scalaz.NonEmptyListFunctions

/**
 * An implementation of {@link CityDao} that uses a JSON resource
 * to store countries and cities.
 */
class JsonResourceCityDao extends CityDao {

  /**
   * A multimap of all countries keyed by their international dialling codes. The collection values of this map are ordered
   * such that countries with more cities are listed first.
   */
  val countriesByInternationalDiallingCode = new OrderedNonEmptyMultimap[Country](
    new HashMap,
    Ordering.by((c: Country) => (-c.cities.length, c.name)))

  /**
   * A multimap of all cities keyed by their international dialling codes. The collection values of this map are ordered
   * such that cities with longer std codes are listed first.
   */
  val citiesByInternationalDiallingCode = new OrderedNonEmptyMultimap[City](
    new HashMap,
    Ordering.by((c: City) => (-c.stdCode.length, c.stdCode, c.name)))

  /**
   * A set of all known international dialling codes. The dialling codes are ordered longest first and then by value.
   */
  val internationalDiallingCodes = new TreeSet[String]()(Ordering.by((s: String) => (-s.length, s)))

  /**
   * A map that holds the country for each city.
   */
  val countriesByCity = new HashMap[City, Country]

  {
    val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
    val in = getClass.getClassLoader.getResourceAsStream("resources/countries.json")
    try {
      val countries: List[Country] = mapper.readValue(in, new TypeReference[List[Country]] {})
      countries.foreach(country => {
        val internationalDiallingCode = country.internationalDiallingCode
        internationalDiallingCodes += internationalDiallingCode
        countriesByInternationalDiallingCode.add(internationalDiallingCode, country)
        country.cities.foreach(city => {
          citiesByInternationalDiallingCode.add(internationalDiallingCode, city)
          countriesByCity += ((city, country))
        })
      })
    } finally {
      in.close
    }
  }

  /**
   * A small class to encapsulate a map whose values are non-empty, sorted lists.
   */
  class OrderedNonEmptyMultimap[C](map: Map[String, NonEmptyList[C]], ordering: Ordering[C]) {

    def add(key: String, value: C): Unit = {
      val newList = map.get(key).map { values =>
        val (smallerValues, largerValues) =
          ((vs: List[C]) => (vs.filter(_ < value), vs.filter(value < _)))(values.list)
        smallerValues <::: NonEmptyList(value) :::> largerValues
      }.
        getOrElse { NonEmptyList(value) }
      map.put(key, newList)
    }

    def get(key: String) = map.get(key)

    implicit class OrderingImplicit(c1: C) {
      def <(c2: C) = ordering.lt(c1, c2)
    }
  }

  override def extractInternationalDiallingCode(number: String) = internationalDiallingCodes.find(number.startsWith(_)).get

  override def extractCity(number: String, internationalDiallingCode: String): Option[City] = {
    val cities = citiesByInternationalDiallingCode.get(internationalDiallingCode)
    cities.map { _.list.find((c: City) => number.startsWith(c.stdCode)) }.getOrElse(None)
  }

  override def countryOf(city: City): Country = {
    countriesByCity.get(city).get
  }

  override def countries(internationalDiallingCode: String): NonEmptyList[Country] = {
    countriesByInternationalDiallingCode.get(internationalDiallingCode).getOrElse {
      throw new IllegalArgumentException(s"${internationalDiallingCode} is not a valid dialling code.")
    }
  }
}
