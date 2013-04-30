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
package uk.co.unclealex.callerid.remote.view

/**
 * A JSON compatible class that represents the telephone number that made a call.
 */

case class Number(
  /**
   * The international prefix of the telephone number that made this call.
   */
  internationalPrefix: String,

  /**
   * The STD code of the telephone number that made this call or none if the number was non-geographic.
   */
  stdCode: Option[String],
  /**
   * The non-geographical part of the telephone number that made this call.
   */
  number: String)