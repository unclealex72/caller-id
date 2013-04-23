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
package uk.co.unclealex.callerid.remote.numbers

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

/**
 * An implementation of {@link CityDao} that uses a JSON resource
 * to store countries and cities.
 */
class JsonResourceCityDao extends CityDao {

  /**
   * A multimap of all countries keyed by their international dialling codes. The collection values of this map are ordered
   * such that countries with more cities are listed first.
   */
  val countriesByInternationalDiallingCode = new OrderedMultimap[Country](
    new HashMap,
    Ordering.by((c: Country) => (-c.cities.length, c.name)))

  /**
   * A multimap of all cities keyed by their international dialling codes. The collection values of this map are ordered
   * such that cities with longer std codes are listed first.
   */
  val citiesByInternationalDiallingCode = new OrderedMultimap[City](
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
    val in = getClass.getClassLoader.getResourceAsStream("countries.json")
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

  class OrderedMultimap[C](map: Map[String, Set[C]], ordering: Ordering[C]) {

    def add(key: String, value: C): Unit = {
      val values = map.getOrElseUpdate(key, { new TreeSet[C]()(ordering) })
      values += value
    }

    def get(key: String) = map.get(key)
  }

  override def extractInternationalDiallingCode(number: String) = internationalDiallingCodes.find(number.startsWith(_)).get

  override def extractCity(number: String, internationalDiallingCode: String): Option[City] = {
    val cities = citiesByInternationalDiallingCode.get(internationalDiallingCode)
    cities.getOrElse(new HashSet[City]).find((c: City) => number.startsWith(c.stdCode))
  }

  override def countryOf(city: City): Country = {
    countriesByCity.get(city).get
  }

  override def countries(internationalDiallingCode: String): List[Country] = {
    countriesByInternationalDiallingCode.get(internationalDiallingCode).map(_.toList).getOrElse(List())
  }
}
