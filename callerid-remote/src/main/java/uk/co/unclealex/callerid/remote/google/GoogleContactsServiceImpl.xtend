/**
 * Copyright 2010 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance
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
package uk.co.unclealex.callerid.remote.google

import com.google.common.base.Optional
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.google.gdata.data.contacts.ContactEntry
import com.google.gdata.data.contacts.ContactFeed
import com.google.gdata.data.extensions.PostalAddress
import com.google.gdata.data.extensions.StructuredPostalAddress
import java.net.URL
import java.util.List
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Pair
import org.springframework.transaction.annotation.Transactional
import uk.co.unclealex.callerid.remote.model.User

import static extension uk.co.unclealex.xtend.OptionalExtensions.*

/**
 * The default implementation of {@link GoogleContactsService}.
 */
@Transactional
class GoogleContactsServiceImpl implements GoogleContactsService {

    @Property val extension GoogleTokenService googleTokenService

    /**
     * The Google constants object used to configure how to get contacts from Google.
     */
    @Property val GoogleConstants googleConstants

    @Inject
    new(GoogleTokenService googleTokenService, GoogleConstants googleConstants) {
        this._googleConstants = googleConstants
        this._googleTokenService = googleTokenService
    }

    def ContactsService createContactsService(User user) {
        return new ContactsService("callerid.unclealex.co.uk", user.accessToken)
    }

    override getAllContacts(User user) {
        val ContactsService contactsService = createContactsService(user)
        val ContactFeed resultFeed = contactsService.getFeed(new URL(googleConstants.contactFeedUrl),
            typeof(ContactFeed))
        Sets::newHashSet(
            resultFeed.entries.filter[hasName].map [
                new GoogleContact(name.fullName.value, findPostalAddress,
                    Sets::newHashSet(phoneNumbers.map[phoneNumber]))
            ])
    }

    /**
     * Find a contact's postal address by looking through it's postal and structured postal addresses, returning
     * a primary address if one exists, an arbitrary address if addresses exist but none or primary or absent if no
     * address information has been held.
     * @param entry The contact being searched.
     * @return A formatted postal address.
     */
    def Optional<String> findPostalAddress(ContactEntry entry) {
        val List<Pair<String, Boolean>> addresses = newArrayList()
        addresses.addAll(entry.postalAddresses.map[toAddress])
        addresses.addAll(entry.structuredPostalAddresses.map[toAddress])
        val List<Pair<String, Boolean>> primaryAddresses = Lists::newArrayList(addresses.filter[value])
        val List<Pair<String, Boolean>> addressesToUse = #[primaryAddresses, addresses].findFirst[!it.empty]
        addressesToUse.head.asOptional.transform([key.replace("\n", ", ")])
    }

    /**
     * Convert a postal address into pair of a flag for whether the address is primary and a formatted string
     * for the address itself.
     * @param postalAddress The address to convert.
     * @return A pair of a flag and a string.
     */
    def Pair<String, Boolean> toAddress(PostalAddress postalAddress) {
        postalAddress.value -> postalAddress.primary
    }

    /**
     * Convert a structured postal address into pair of a flag for whether the address is primary and a formatted string
     * for the address itself.
     * @param spa The address to convert.
     * @return A pair of a flag and a string.
     */
    def Pair<String, Boolean> toAddress(StructuredPostalAddress spa) {
        spa.formattedAddress.value -> if(spa.hasPrimary) spa.primary else false
    }

}
