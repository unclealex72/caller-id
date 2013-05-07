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
package uk.co.unclealex.callerid.remote.controller

import org.springframework.stereotype.Controller
import uk.co.unclealex.callerid.remote.call.ReceivedCall
import uk.co.unclealex.callerid.remote.call.CallReceivedService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import uk.co.unclealex.callerid.remote.number.NumberFormatter

/**
 * A Spring controller that receives a non-normalised telephone number, serialises it and returns a string
 * representation of the call that was made.
 */
@Controller
class CallReceivedController(
  /**
   * The call received service used to actually received calls.
   */
  callReceivedService: CallReceivedService,
  /**
   * The number formatter used to format the string sent back to the client.
   */
  numberFormatter: NumberFormatter) {

  @RequestMapping(value = Array("/callreceived/{number}"), produces = Array("text/string"))
  @ResponseBody
  def callReceived(@PathVariable("number") number: String): String = {
    val receivedCall = callReceivedService.callReceived(number)
    receivedCall.contact.map(_.name).getOrElse {
      val phoneNumber = receivedCall.phoneNumber
      val formattedNumber = numberFormatter.formatNumber(phoneNumber).mkString(" ")
      val formattedAddress = numberFormatter.formatAddress(phoneNumber).mkString(", ")
      s"${formattedNumber} (${formattedAddress})"
    }
  }
}