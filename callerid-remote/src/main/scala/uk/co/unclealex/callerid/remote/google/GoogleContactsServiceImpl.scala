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

import scala.collection.JavaConversions._

import org.springframework.transaction.annotation.Transactional
/**
 * The default implementation of {@link GoogleContactsService}.
 */
@Transactional
class GoogleContactsServiceImpl(
  /**
   * The service used to get tokens from Google.
   */
  googleTokenService: GoogleTokenService,
  /**
   * The Google constants object used to configure how to get contacts from Google.
   */
  googleConstants: GoogleConstants) /*extends GoogleContactsService*/ {

  /*
  override def getAllContacts(user: User): Set[GoogleContact] = {
    val url = new UrlWithParameters(googleConstants.contactFeedUrl)
      .withParameters(("accessToken", googleTokenService.accessToken(user)), ("max-results", Int.MaxValue)).toURL
    val resultFeed = new Service().getFeed(url, classOf[ContactFeed])
    val hasTitleFilter = (ce: ContactEntry) => !ce.getTitle().getPlainText().trim().isEmpty()
    val googleContactMapper = (ce: ContactEntry) =>
      GoogleContact(
        ce.getTitle().getPlainText().trim(),
        findPostalAddress(ce),
        ce.getPhoneNumbers().map(p => p.getPhoneNumber()).toSet)
    resultFeed.getEntries().filter(hasTitleFilter).map(googleContactMapper).toSet
  }

  /**
   * Find a contact's postal address by looking through it's postal and structured postal addresses, returning
   * a primary address if one exists, an arbitrary address if addresses exist but none or primary or absent if no
   * address information has been held.
   * @param entry The contact being searched.
   * @return A formatted postal address.
   */
  def findPostalAddress(entry: ContactEntry): Option[String] = {
    val addresses = (entry.getPostalAddresses().map(toAddress(_)) ++ entry.getStructuredPostalAddresses().map(toAddress(_))).toList
    val primaryAddresses = addresses.filter(_._2)
    val addressesToUse = List(primaryAddresses, addresses).map(_.map(_._1)).find(!_.isEmpty)
    if (addressesToUse.isEmpty) {
      None
    } else {
      addressesToUse.get.find(s => true).map(_.replace("\n", " "))
    }
  }

  /**
   * Convert a postal address into pair of a flag for whether the address is primary and a formatted string
   * for the address itself.
   * @param postalAddress The address to convert.
   * @return A pair of a flag and a string.
   */
  def toAddress(postalAddress: PostalAddress): Pair[String, Boolean] =
    new Pair(postalAddress.getValue(), postalAddress.getPrimary())

  /**
   * Convert a structured postal address into pair of a flag for whether the address is primary and a formatted string
   * for the address itself.
   * @param spa The address to convert.
   * @return A pair of a flag and a string.
   */
  def toAddress(spa: StructuredPostalAddress): Pair[String, Boolean] =
    new Pair(spa.getFormattedAddress().getValue(), if (spa.hasPrimary) spa.getPrimary else false)
    * 
    */
}
