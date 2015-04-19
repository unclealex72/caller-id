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

package legacy.module

import legacy.controllers.WebServiceSecurityConfiguration

import scala.collection.JavaConversions.asScalaBuffer
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.tzavellas.sse.guice.ScalaModule
import legacy.remote.call.CallReceivedService
import legacy.remote.call.CallReceivedServiceImpl
import legacy.remote.call.ReceivedCallsService
import legacy.remote.call.ReceivedCallsServiceImpl
import legacy.remote.contact.ContactService
import legacy.remote.contact.ContactServiceImpl
import legacy.remote.dao.CallRecordDao
import legacy.remote.dao.SquerylCallRecordDao
import legacy.remote.dao.SquerylUserDao
import legacy.remote.dao.UserDao
import legacy.remote.google.GoogleAuthorisationService
import legacy.remote.google.GoogleConfiguration
import legacy.remote.google.GoogleConstants
import legacy.remote.google.GoogleContactsParser
import legacy.remote.google.GoogleContactsParserImpl
import legacy.remote.google.GoogleContactsService
import legacy.remote.google.GoogleContactsServiceImpl
import legacy.remote.google.GoogleRequestService
import legacy.remote.google.GoogleTokenService
import legacy.remote.google.GoogleTokenServiceImpl
import legacy.remote.google.JerseyGoogleRequestService
import legacy.remote.google.NowService
import legacy.remote.google.StaticGoogleAuthorisationService
import legacy.remote.google.SystemNowService
import legacy.remote.number.CityDao
import legacy.remote.number.JsonResourceCityDao
import legacy.remote.number.LocationConfiguration
import legacy.remote.number.NumberFormatter
import legacy.remote.number.NumberFormatterImpl
import legacy.remote.number.NumberLocationService
import legacy.remote.number.NumberLocationServiceImpl

/**
 * @author alex
 *
 */
class CallerIdModule extends ScalaModule {

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
      StaticGoogleAuthorisationService(conf.getString("users").split(",").toList)
    }
    bindConfiguration[LocationConfiguration]("location") { conf =>
      LocationConfiguration(conf.getString("internationalCode"), conf.getString("stdCode"))
    }
    bindConfiguration[WebServiceSecurityConfiguration]("security") { conf =>
      WebServiceSecurityConfiguration(conf.getString("username"), conf.getString("password"))
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
