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
 * A JSON compatible class that represents the telephone number that made a call.
 */
@JsonSerialize(include=Inclusion::NON_NULL)
 @Data class Number {
    /**
    * The international prefix of the telephone number that made this call.
    */
    val String internationalPrefix

    /**
    * The STD code of the telephone number that made this call or null if the number was non-geographic.
    */
    val String stdCode

    /**
    * The non-geographical part of the telephone number that made this call.
    */
    val String number

    @JsonCreator
    new(
        @NotNull @JsonProperty("internationalPrefix") String internationalPrefix,
        @JsonProperty("stdCode") String stdCode,
        @NotNull @JsonProperty("number") String number
    ) {
        super();
        this._internationalPrefix = internationalPrefix
        this._stdCode = stdCode
        this._number = number
    }

}
