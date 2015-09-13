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

/**
 * A JSON serialisable instance of LocationConfiguration.
 */
case class LocationConfiguration(internationalCode : String, stdCode : String)

object LocationConfiguration {

  sealed abstract class Implicits[C](c: C, localCodeFactory: LocationConfiguration => String, codeFactory: C => String) {
    def isLocal(implicit locationConfiguration: LocationConfiguration): Boolean =
      codeFactory(c) == localCodeFactory(locationConfiguration)
    def isNotLocal(implicit locationConfiguration: LocationConfiguration) = !isLocal
  }

  implicit class CityImplicits(c: City) extends Implicits[City](c, _.stdCode, _.stdCode)
  implicit class CountryImplicits(c: Country) extends Implicits[Country](c, _.internationalCode, _.internationalDiallingCode)
}