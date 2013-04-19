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

import java.util.List
import org.codehaus.jackson.annotate.JsonCreator
import org.codehaus.jackson.annotate.JsonProperty

/**
 * An immutable bean containing information about the country call came from.
 */
@Data class Country {
    /**
     * The country's name.
     */
    val String name

    /**
     * The country's international dialling code.
     */
    val String internationalDiallingCode

    /**
     * They country's ISO 3166-1 alpha-2 code.
     */
    val String isoCode

    /**
     * The known cities in this country, sorted by longest STD codes first.
     */
    val List<City> cities;

    @JsonCreator
    new(@JsonProperty("name") String name, @JsonProperty("internationalDiallingCode") String internationalDiallingCode,
        @JsonProperty("isoCode") String isoCode, @JsonProperty("cities") List<City> cities) {
        this._name = name
        this._internationalDiallingCode = internationalDiallingCode
        this._isoCode = isoCode
        this._cities = cities
    }
}
