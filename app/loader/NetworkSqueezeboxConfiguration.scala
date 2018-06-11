package loader

import scala.concurrent.duration.FiniteDuration

case class NetworkSqueezeboxConfiguration(host: String, port: Int, duration: FiniteDuration)