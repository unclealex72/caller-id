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

package uk.co.unclealex.callerid.local.main

import scala.reflect.ClassTag

import com.google.inject.Provider
import com.tzavellas.sse.guice.ScalaModule

import uk.co.unclealex.callerid.local.call.CallAlerter
import uk.co.unclealex.callerid.local.call.CallController
import uk.co.unclealex.callerid.local.call.CallControllerImpl
import uk.co.unclealex.callerid.local.call.JerseyCallAlerter
import uk.co.unclealex.callerid.local.configuration.ConfigurationFactory
import uk.co.unclealex.callerid.local.configuration.ModemConfiguration
import uk.co.unclealex.callerid.local.configuration.RemoteConfiguration
import uk.co.unclealex.callerid.local.configuration.SqueezeboxConfiguration
import uk.co.unclealex.callerid.local.device.BufferedIoDevice
import uk.co.unclealex.callerid.local.device.DataStreamIoDevice
import uk.co.unclealex.callerid.local.device.Io
import uk.co.unclealex.callerid.local.device.IoDevice
import uk.co.unclealex.callerid.local.device.SocketIo
import uk.co.unclealex.callerid.local.modem.ModemListenerImpl
import uk.co.unclealex.callerid.local.squeezebox.Squeezebox
import uk.co.unclealex.callerid.local.squeezebox.SqueezeboxImpl

/**
 * The main Guice module used to run the application
 * @author alex
 *
 */
class DefaultModule extends ScalaModule {

  override def configure = {
    val configurationResourceName = "configuration.json"
    val configurationUrl = getClass.getClassLoader.getResource(configurationResourceName)
    require(configurationUrl != null, s"Resource $configurationResourceName is missing.")
    val configurationFactory = new ConfigurationFactory(configurationUrl)

    def bindDevice[C](
      name: String, socketFactory: C => (String, Int), ioDeviceProvider: Io => IoDevice)(implicit c: ClassTag[C]) {
      val configuration = configurationFactory[C]
      val socketInfo = socketFactory(configuration)
      val provider = new Provider[IoDevice]() {
        def get = ioDeviceProvider(new SocketIo(socketInfo._1, socketInfo._2))
      }
      bind[IoDevice].annotatedWithName(name).toProvider(provider)
    }

    // The modem
    bindDevice[ModemConfiguration]("modem", mc => (mc.modemHost, mc.modemPort), io => new DataStreamIoDevice(io))
    bind[Runnable].to[ModemListenerImpl]

    // Calls
    bind[RemoteConfiguration].toInstance(configurationFactory[RemoteConfiguration])
    bind[CallController].to[CallControllerImpl]
    bind[CallAlerter].to[JerseyCallAlerter]

    // The squeezebox
    bindDevice[SqueezeboxConfiguration](
      "squeezebox", sc => (sc.squeezeboxHost, sc.squeezeboxPort), io => new BufferedIoDevice(io))
    bind[Squeezebox].to[SqueezeboxImpl]
  }
}