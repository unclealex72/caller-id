package loader

sealed trait ModemConfiguration

object DebugModemConfiguration extends ModemConfiguration

case class NetworkModemConfiguration(host: String, port: Int) extends ModemConfiguration