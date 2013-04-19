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

import javax.validation.constraints.NotNull
import org.codehaus.jackson.annotate.JsonCreator
import org.codehaus.jackson.annotate.JsonProperty
import org.codehaus.jackson.map.annotate.JsonSerialize
import org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion

/**
 * A call is a JSON representation of all known details about a call record. This can then be interrogated
 * and displayed by a client.
 */
 @JsonSerialize(include=Inclusion::NON_NULL)
 @Data class Call {
   
   /**
    * The ISO 8601 date and time this call was received.
    */
   val String time

   /**
    * The telephone number of the caller who made this call.
    */
   val Number number      

   /**
    * The originating location of this call. This will always contain at least the country of origin.
    */
   val Location location
   
   /**
    * The contact who called, if known.
    */
   val Contact contact
   
  @JsonCreator
  new(@NotNull @JsonProperty("time") String time,
      @NotNull @JsonProperty("number") Number number,
      @NotNull @JsonProperty("location") Location location,
      @JsonProperty("contact") Contact contact
  ) {
    super();
    this._time = time
    this._number = number
    this._location = location
    this._contact = contact
  }
   
}