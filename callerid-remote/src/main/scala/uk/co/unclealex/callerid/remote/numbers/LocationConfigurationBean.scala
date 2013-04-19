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
package uk.co.unclealex.callerid.remote.numbers

import javax.validation.constraints.NotNull
import org.codehaus.jackson.annotate.JsonCreator
import org.codehaus.jackson.annotate.JsonProperty
import uk.co.unclealex.callerid.remote.numbers.LocationConfiguration

/**
 * A JSON serialisable instance of LocationConfiguration.
 */
@Data class LocationConfigurationBean implements LocationConfiguration {

    /**
     * The international code for the phone receiving calls.
     */
    val String internationalCode
    
    /**
     * The STD code for the phone receiving calls.
     */
    val String stdCode
    
    @JsonCreator
    new(
        @NotNull @JsonProperty("internationalCode") String internationalCode,
        @NotNull @JsonProperty("stdCode") String stdCode
    ) {
        this._internationalCode = internationalCode
        this._stdCode = stdCode
    }
}
