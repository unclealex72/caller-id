package loader

import scala.concurrent.duration.FiniteDuration

/**
  * Configuration for the squeezebox server configuration.
  * @param host The squeezebox host.
  * @param port The squeezebox port.
  * @param duration The amount of time any messages should be displayed.
  */
case class NetworkSqueezeboxConfiguration(host: String, port: Int, duration: FiniteDuration)