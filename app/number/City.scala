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

import argonaut.Argonaut._
import argonaut.CodecJson

/**
 * An immutable bean containing information about the city a geographic call came from.
 */
case class City(
  /**
   * The city's name.
   */
  name: String,
  /**
   * The city's STD dialling code.
   */
  stdCode: String)

object City {

  implicit def CityCodec: CodecJson[City] = casecodec2(City.apply, City.unapply)("name", "stdCode")
}