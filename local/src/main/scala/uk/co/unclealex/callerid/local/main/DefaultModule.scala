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

import java.net.Socket
import java.nio.charset.Charset

import scala.reflect.ClassTag

import com.tzavellas.sse.guice.ScalaModule

import uk.co.unclealex.callerid.local.call.CallAlerter
import uk.co.unclealex.callerid.local.call.CallController
import uk.co.unclealex.callerid.local.call.CallControllerImpl
import uk.co.unclealex.callerid.local.call.JerseyCallAlerter
import uk.co.unclealex.callerid.local.configuration.ConfigurationFactory
import uk.co.unclealex.callerid.local.configuration.ModemConfiguration
import uk.co.unclealex.callerid.local.configuration.RemoteConfiguration
import uk.co.unclealex.callerid.local.configuration.SqueezeboxConfiguration
import uk.co.unclealex.callerid.local.device.Device
import uk.co.unclealex.callerid.local.device.NetworkDevice
import uk.co.unclealex.callerid.local.modem.ModemListener
import uk.co.unclealex.callerid.local.squeezebox.Squeezebox
import uk.co.unclealex.callerid.local.squeezebox.SqueezeboxImpl

/**
 * The main Guice module used to run the application
 * @author alex
 *
 */
class DefaultModule(
  /**
   * The factory used to create a socket from a name, a host and port.
   */
  socketFactory: (String, String, Int) => Socket) extends ScalaModule {

  val configurationFactory = new ConfigurationFactory(getClass.getClassLoader.getResource("configuration.json"))

  override def configure = {

    // The modem
    bindDevice[ModemConfiguration]("modemDevice", mc => mc.modemHost, mc => mc.modemPort)
    bind[ModemListener]

    // Calls
    bind[RemoteConfiguration].toInstance(configurationFactory[RemoteConfiguration])
    bind[CallController].to[CallControllerImpl]
    bind[CallAlerter].to[JerseyCallAlerter]

    // The squeezebox
    bindDevice[SqueezeboxConfiguration]("squeezeboxDevice", sc => sc.squeezeboxHost, sc => sc.squeezeboxPort)
    bind[Squeezebox].to[SqueezeboxImpl]
  }

  def bindDevice[C](name: String, hostFactory: C => String, portFactory: C => Int)(implicit c: ClassTag[C]) {
    val configuration = configurationFactory[C]
    val socket = socketFactory(name, hostFactory(configuration), portFactory(configuration))
    bind[Device].annotatedWithName(name).toInstance(new NetworkDevice(socket, Charset.forName("utf-8")))
  }
}

/**
 * The default module's companion object.
 */
object DefaultModule {

  /**
   * Create the default module with a hardcoded socket factory.
   */
  def apply: DefaultModule = apply((name: String, host: String, port: Int) => new Socket(host, port))

  /**
   * Create the default module with a mockable socket factory.
   */
  def apply(socketFactory: (String, String, Int) => Socket): DefaultModule = new DefaultModule(socketFactory)
}