package loader

/**
  * A model used to configure the modem.
  */
sealed trait ModemConfiguration

/**
  * Indicate that the "debug" modem is to be used so that calls can be pushed via a REST API.
  */
object DebugModemConfiguration extends ModemConfiguration

/**
  * The configuration for a modem listening on a TCP port.
  * @param host The modem's host.
  * @param port The modem's port.
  */
case class NetworkModemConfiguration(host: String, port: Int) extends ModemConfiguration