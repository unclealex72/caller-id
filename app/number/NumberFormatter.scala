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
 * @author unclealex72
 *
 */

package number

import cats.data.NonEmptyList

/**
 * A trait for formatting telephone numbers.
 *
 * @author alex
 *
 */
trait NumberFormatter {

  /**
    * Format a telephone number as a list of strings (so any separator can be applied later).
    * Numbers will be formatted as follows:
    * <table>
    * <th><td>Number Type</td><td>Format</td></th>
    * <tr><td>International, Geographic</td><td>+xx yyy zzzzzz</td></tr>
    * <tr><td>International, Non-geographic</td><td>+xx yyyzzzzzz</td></tr>
    * <tr><td>National, Geographic</td><td>0yyy zzzzzz</td></tr>
    * <tr><td>National, Non-geographic</td><td>0yyyzzzzzz</td></tr>
    * <tr><td>Local</td><td>0yyy zzzzzz</td></tr>
    * </table>
    */
  def formatNumber(countries: NonEmptyList[Country], maybeCity: Option[City], number: String): String
}