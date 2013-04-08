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
package uk.co.unclealex.callerid.remote.numbers;

/**
 * The default implementation of {@link NumberLocationService}.
 */
class NumberLocationServiceImpl implements NumberLocationService {
    /**
     * The {@link CityDao} used to find cities and countries from telephone numbers.
     */
    @Property var extension CityDao cityDao

    /**
     * The {@link LocationConfiguration} of the telephone number that is receiving calls.
     */
    @Property var LocationConfiguration locationConfiguration

    override decompose(String number) {
        #["00" -> international, "+" -> international, "0" -> national, "" -> local].map [
            val prefix = key
            val decomposer = value
            if (number.startsWith(prefix)) {
                decomposer.apply(number.substring(prefix.length))
            }
        ].findFirst[it != null]
    }

    /**
     * Convert an international string phone number into a normalised phone number.
     * @return A function that converts an international string phone number into a normalised phone number.
     */
    def Functions$Function1<String, PhoneNumber> international() {
        [toPhoneNumber(it)]
    }

    /**
     * Convert a national string phone number into a normalised phone number.
     * @return A function that converts a national string phone number into a normalised phone number.
     */
    def Functions$Function1<String, PhoneNumber> national() {
        [toPhoneNumber('''«locationConfiguration.internationalCode»«it»''')]
    }

    /**
     * Convert a local string phone number into a normalised phone number.
     * @return A function that converts a local string phone number into a normalised phone number.
     */
    def Functions$Function1<String, PhoneNumber> local() {
        [toPhoneNumber('''«locationConfiguration.internationalCode»«locationConfiguration.stdCode»«it»''')]
    }

    /**
     * Convert a phone number containing the international dialling code (without a 00 or + prefix), 
     * the std code (without the 0 prefix)
     * and the rest of the number into a normalised phone number.
     * @param The number to convert.
     * @return A normalised phone number.
     */
    def PhoneNumber toPhoneNumber(String number) {
        val String normalisedNumber = "+" + number
        val String internationalDiallingCode = number.extractInternationalDiallingCode
        val String nationalNumber = number.substring(internationalDiallingCode.length)
        val City city = nationalNumber.extractCity(internationalDiallingCode)
        if (city == null) {
            new PhoneNumber(normalisedNumber, internationalDiallingCode.countries, null, nationalNumber)
        } else {
            new PhoneNumber(normalisedNumber, #[city.country], city, nationalNumber.substring(city.stdCode.length))
        }
    }
}
