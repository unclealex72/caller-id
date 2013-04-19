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

import com.google.common.collect.Multimap
import com.google.common.collect.Ordering
import com.google.common.collect.Sets
import com.google.common.collect.TreeMultimap
import java.io.InputStream
import java.util.List
import java.util.Map
import java.util.SortedSet
import javax.annotation.PostConstruct
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.type.TypeFactory

import static extension uk.co.unclealex.xtend.OrderingExtensions.*
import static extension uk.co.unclealex.xtend.OptionalExtensions.*

import static extension uk.co.unclealex.xtend.ObjectExtensions.*

/**
 * An implementation of {@link CityDao} that uses a JSON resource
 * to store countries and cities.
 */
class JsonResourceCityDao implements CityDao {
    /**
     * A multimap of all countries keyed by their international dialling codes. The collection values of this map are ordered
     * such that countries with more cities are listed first.
     */
    @Property var Multimap<String, Country> countriesByInternationalDiallingCode = TreeMultimap::create(
        Ordering::natural, 
        typeof(Country) <=> [-compareBy[cities.length] + compareBy[name]])

    /**
     * A multimap of all cities keyed by their international dialling codes. The collection values of this map are ordered
     * such that cities with longer std codes are listed first.
     */
    @Property var Multimap<String, City> citiesByInternationalDiallingCode = TreeMultimap::create(
        Ordering::natural, typeof(City) <=> [compareBy[-stdCode.length] + compareBy([stdCode], [name])])

    /**
     * A set of all known international dialling codes. The dialling codes are ordered longest first and then by value.
     */
    @Property var SortedSet<String> internationalDiallingCodes = Sets::newTreeSet(
        -typeof(String).compareBy[length] + Ordering::natural)

    /**
     * A map that holds the country for each city.
     */
    @Property var Map<City, Country> countriesByCity = newHashMap()

    /**
     * Load all the countries from a JSON resource.
     */
    @PostConstruct
    def void loadCountries() {
        val ObjectMapper mapper = new ObjectMapper
        val InputStream in = typeof(JsonResourceCityDao).classLoader.getResourceAsStream("countries.json")
        try {
            val List<Country> countries = mapper.readValue(in,
                TypeFactory::collectionType(typeof(List), typeof(Country)))
            countries.forEach [ Country country |
                val internationalDiallingCode = country.internationalDiallingCode
                internationalDiallingCodes.add(internationalDiallingCode)
                countriesByInternationalDiallingCode.put(internationalDiallingCode, country)
                citiesByInternationalDiallingCode.putAll(internationalDiallingCode, country.cities)
                country.cities.forEach[City city|countriesByCity.put(city, country)]
            ]

        } finally {
            in.close
        }
    }

    override extractInternationalDiallingCode(String number) {
        internationalDiallingCodes.findFirst[number.startsWith(it)]
    }

    override extractCity(String number, String internationalDiallingCode) {
        citiesByInternationalDiallingCode.get(internationalDiallingCode).findFirst[number.startsWith(stdCode)].
            asOptional
    }

    override country(City city) {
        countriesByCity.get(city)
    }

    override countries(String internationalDiallingCode) {
        countriesByInternationalDiallingCode.get(internationalDiallingCode)
    }
}
