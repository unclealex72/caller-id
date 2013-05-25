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

import java.net.ConnectException
import java.net.Socket
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
import uk.co.unclealex.callerid.local.device.IoDevice
import uk.co.unclealex.callerid.local.modem.ModemListenerImpl
import uk.co.unclealex.callerid.local.squeezebox.Squeezebox
import uk.co.unclealex.callerid.local.squeezebox.SqueezeboxImpl
import uk.co.unclealex.callerid.local.device.SocketIoDevice

/**
 * The main Guice module used to run the application
 * @author alex
 *
 */
class DefaultModule(
  /**
   * The factory used to create a socket from a name, a host and port.
   */
  ioDeviceFactory: (String, String, Int) => IoDevice) extends ScalaModule {

  override def configure = {
    val configurationResourceName = "configuration.json"
    val configurationUrl = getClass.getClassLoader.getResource(configurationResourceName)
    require(configurationUrl != null, s"Resource $configurationResourceName is missing.")
    val configurationFactory = new ConfigurationFactory(configurationUrl)

    def bindDevice[C](name: String, hostFactory: C => String, portFactory: C => Int)(implicit c: ClassTag[C]) {
      val configuration = configurationFactory[C]
      val ioDevice = ioDeviceFactory(name, hostFactory(configuration), portFactory(configuration))
      bind[IoDevice].annotatedWithName(name).toInstance(ioDevice)
    }

    // The modem
    bindDevice[ModemConfiguration]("modem", mc => mc.modemHost, mc => mc.modemPort)
    bind[Runnable].to[ModemListenerImpl]

    // Calls
    bind[RemoteConfiguration].toInstance(configurationFactory[RemoteConfiguration])
    bind[CallController].to[CallControllerImpl]
    bind[CallAlerter].to[JerseyCallAlerter]

    // The squeezebox
    bindDevice[SqueezeboxConfiguration]("squeezebox", sc => sc.squeezeboxHost, sc => sc.squeezeboxPort)
    bind[Squeezebox].to[SqueezeboxImpl]
  }

}

/**
 * The default module's companion object.
 */
object DefaultModule {

  /**
   * Create the default module with a hardcoded socket factory.
   */
  def apply(): DefaultModule = apply((name: String, host: String, port: Int) => new SocketIoDevice(host, port))

  /**
   * Create the default module with a mockable socket factory.
   */
  def apply(ioDeviceFactory: (String, String, Int) => IoDevice): DefaultModule = new DefaultModule(ioDeviceFactory)
}