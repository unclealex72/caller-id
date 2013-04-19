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
package uk.co.unclealex.callerid.remote.google

import org.codehaus.jackson.annotate.JsonCreator
import javax.validation.constraints.NotNull
import org.codehaus.jackson.annotate.JsonProperty

/**
 * A JSON serialisable instance of GoogleConfiguration.
 */
@Data class GoogleConfigurationBean implements GoogleConfiguration {
    /**
     * The private consumer secret for this application.
     */
    val String consumerSecret

    /**
     * The public consumer ID for this application.
     */
    val String consumerId

    @JsonCreator
    new(
        @NotNull @JsonProperty("consumerSecret") String consumerSecret,
        @NotNull @JsonProperty("consumerId") String consumerId
    ) {
        this._consumerSecret = consumerSecret
        this._consumerId = consumerId
    }
}
