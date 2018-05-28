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

import org.scalatest._

class CityDaoImplTest extends WordSpec with Matchers {

  val cityDao: CityDao = new CityDaoImpl(Countries().countries)


  "Extracting the international code" should {
    "find 44 for a British number" in {
      cityDao.extractInternationalDiallingCode("441256118118") should ===(Some("44"))
    }
  }

  "Extracting UK cities" should {
    Seq("1697500500" -> "Brampton", "1697300300" -> "Wigton", "1697400400" -> "Raughton Head").foreach {
      case (number, expectedCityName) =>
        s"return $expectedCityName for $number" in {
          cityDao.extractCity(number, "44").map(_.name) should ===(Some(expectedCityName))
        }
    }
  }

  "Getting a country for a city" should {
    "return Basingstoke for 1256" in {
      val basingstoke = City(name = "Basingstoke", stdCode = "1256")
      cityDao.countryOf(basingstoke).map(_.name) should ===(Some("United Kingdom"))
    }
  }

  "Getting countries for an international dialling code" should {
    "return all the countries in size order" in {
      val countries = cityDao.countries("44").map(_.toList).getOrElse(List.empty[Country])
      countries.map(c => c.name) should contain inOrderOnly("United Kingdom", "Guernsey", "Isle of Man", "Jersey")
    }
  }
}
