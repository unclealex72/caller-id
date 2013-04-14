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
package uk.co.unclealex.callerid.remote.contact

import com.google.common.base.Optional
import org.eclipse.xtext.xbase.lib.Functions
import uk.co.unclealex.callerid.remote.google.GoogleContact
import uk.co.unclealex.callerid.remote.numbers.PhoneNumber
import uk.co.unclealex.callerid.remote.view.Contact

/**
 * An interface for finding {@link Contact}s from {@link PhoneNumber}s.
 */
interface ContactService {
    /**
     * Convert a {@link PhoneNumber} into a {@link GoogleContact}. The functional approach is used as to avoid
     * having to create Spring prototype factories.
     * @return A function that can convert a {@link PhoneNumber} into a {@link GoogleContact} by first preloading all
     * Google contacts. It is undefined which contact is returned if the same phone number is shared by more than one
     * contact.
     */
    def Functions$Function1<PhoneNumber, Optional<GoogleContact>> asContacts();

}
