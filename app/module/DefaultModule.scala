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

package module

import scala.collection.JavaConversions.asScalaBuffer

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.tzavellas.sse.guice.ScalaModule

import uk.co.unclealex.callerid.remote.call.CallReceivedService
import uk.co.unclealex.callerid.remote.call.CallReceivedServiceImpl
import uk.co.unclealex.callerid.remote.call.ReceivedCallsService
import uk.co.unclealex.callerid.remote.call.ReceivedCallsServiceImpl
import uk.co.unclealex.callerid.remote.contact.ContactService
import uk.co.unclealex.callerid.remote.contact.ContactServiceImpl
import uk.co.unclealex.callerid.remote.dao.CallRecordDao
import uk.co.unclealex.callerid.remote.dao.SquerylCallRecordDao
import uk.co.unclealex.callerid.remote.dao.SquerylUserDao
import uk.co.unclealex.callerid.remote.dao.UserDao
import uk.co.unclealex.callerid.remote.google.GoogleAuthorisationService
import uk.co.unclealex.callerid.remote.google.GoogleConfiguration
import uk.co.unclealex.callerid.remote.google.GoogleConstants
import uk.co.unclealex.callerid.remote.google.GoogleContactsParser
import uk.co.unclealex.callerid.remote.google.GoogleContactsParserImpl
import uk.co.unclealex.callerid.remote.google.GoogleContactsService
import uk.co.unclealex.callerid.remote.google.GoogleContactsServiceImpl
import uk.co.unclealex.callerid.remote.google.GoogleRequestService
import uk.co.unclealex.callerid.remote.google.GoogleTokenService
import uk.co.unclealex.callerid.remote.google.GoogleTokenServiceImpl
import uk.co.unclealex.callerid.remote.google.JerseyGoogleRequestService
import uk.co.unclealex.callerid.remote.google.NowService
import uk.co.unclealex.callerid.remote.google.StaticGoogleAuthorisationService
import uk.co.unclealex.callerid.remote.google.SystemNowService
import uk.co.unclealex.callerid.remote.number.CityDao
import uk.co.unclealex.callerid.remote.number.JsonResourceCityDao
import uk.co.unclealex.callerid.remote.number.LocationConfiguration
import uk.co.unclealex.callerid.remote.number.NumberFormatter
import uk.co.unclealex.callerid.remote.number.NumberFormatterImpl
import uk.co.unclealex.callerid.remote.number.NumberLocationService
import uk.co.unclealex.callerid.remote.number.NumberLocationServiceImpl

/**
 * @author alex
 *
 */
class DefaultModule extends ScalaModule {

  /**
   * The configuration object supplied with this application.
   */
  val config = ConfigFactory.load()

  override def configure {
    bind[CallReceivedService].to[CallReceivedServiceImpl]
    bind[NumberLocationService].to[NumberLocationServiceImpl]
    bind[CityDao].to[JsonResourceCityDao]
    bind[NumberFormatter].to[NumberFormatterImpl]
    bind[NumberLocationService].to[NumberLocationServiceImpl]
    bind[ContactService].to[ContactServiceImpl]
    bind[ReceivedCallsService].to[ReceivedCallsServiceImpl]

    // Persistence
    bind[UserDao].to[SquerylUserDao]
    bind[CallRecordDao].to[SquerylCallRecordDao]

    // Google
    bind[GoogleContactsParser].to[GoogleContactsParserImpl]
    bind[GoogleContactsService].to[GoogleContactsServiceImpl]
    bind[GoogleTokenService].to[GoogleTokenServiceImpl]
    bind[GoogleRequestService].to[JerseyGoogleRequestService]
    bind[NowService].to[SystemNowService]

    // Configuration
    bindConfiguration[GoogleAuthorisationService]("valid-users") { conf =>
      StaticGoogleAuthorisationService(conf.getList("users").unwrapped().map((v: Any) => v.toString).toList)
    }
    bindConfiguration[LocationConfiguration]("location") { conf =>
      LocationConfiguration(conf.getString("internationalCode"), conf.getString("stdCode"))
    }
    bindConfiguration[GoogleConfiguration]("google") { conf =>
      GoogleConfiguration(
        consumerSecret = conf.getString("consumerSecret"),
        consumerId = conf.getString("consumerId"),
        callbackUrl = conf.getString("callbackUrl"))
    }
    bind[GoogleConstants].toInstance(GoogleConstants.default)
  }

  def bindConfiguration[T](root: String)(block: Config => T)(implicit m: Manifest[T]): Unit = {
    bind(m.runtimeClass.asInstanceOf[Class[T]]).toInstance(block(config getConfig root))
  }
}
