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
  import play.api.libs.json._

  implicit val cityReads: Reads[City] = Json.reads[City]
  implicit val cityWrites: Writes[City] = Json.writes[City]

  /**
    * Order cities so that cities with longer STD codes are smaller.
    */
  implicit val cityOrdering: Ordering[City] = Ordering.by(c => (-c.stdCode.length, c.stdCode, c.name))
}
