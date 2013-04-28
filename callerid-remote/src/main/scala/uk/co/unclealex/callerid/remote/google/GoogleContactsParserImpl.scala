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

package uk.co.unclealex.callerid.remote.google

import java.net.URL
import scala.collection.immutable.Set
import javax.xml.parsers.DocumentBuilderFactory
import scala.xml.XML
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.xml.Node
import uk.co.unclealex.callerid.remote.numbers.NumberLocationService

/**
 * @author alex
 *
 */
class GoogleContactsParserImpl extends GoogleContactsParser {

  override def parse(url: URL): Set[GoogleContact] = {
    val feed: Elem = XML.load(url)
    val contacts: NodeSeq = feed \\ "entry" filter { hasChildrenWithText("title", "phoneNumber") }
    contacts.map(toGoogleContact).toSet
  }

  def hasChildrenWithText(childNames: String*): Node => Boolean = { node =>
    def hasChildWithText: String => Boolean = { childName =>
      (node \ childName find { !_.text.trim.isEmpty }).isDefined
    }
    childNames.map(hasChildWithText).foldLeft(true)(_ && _)
  }

  def toGoogleContact: Node => GoogleContact = { node =>
    val name = (node \ "title" text).trim()
    val phoneNumbers = node \ "phoneNumber" map { phoneNumberNode: Node =>
      val phoneNumberText = phoneNumberNode.text.trim()
      if (phoneNumberText.isEmpty) None else Some(phoneNumberText)
    } filter { _.isDefined } map { _.get }
    val address = node \ "postalAddress" map { addressNode: Node =>
      addressNode.text.split('\n').map { _.trim() }.filterNot { _.isEmpty } mkString (", ")
    }
    new GoogleContact(name, if (address.isEmpty) None else Some(address(0)), phoneNumbers.toSet)
  }
}