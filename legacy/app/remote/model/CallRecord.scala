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
package legacy.remote.model;

import java.util.Date
import org.squeryl.KeyedEntity
import java.sql.Timestamp

/**
 * A persisted representation of a call that has been received.
 */
case class CallRecord(
  /**
   * The ID of the call record
   */
  var id: Long,
  /**
   * The date and time at which this call was received.
   */
  var callDate: Timestamp,
  /**
   * The phone number that called.
   */
  var telephoneNumber: String) extends KeyedEntity[Long]

object CallRecord {

  def apply(callDate: Date, telephoneNumber: String) = new CallRecord(0, new Timestamp(callDate.getTime()), telephoneNumber)
}