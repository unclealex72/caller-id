package device

import util.Provider

/**
 * A provider for network io.
 * Created by alex on 31/08/15.
 */
class SocketIoProvider(host: String, port: Int) extends Provider[IoDevice] {

  override def get = BufferedIoDevice(new SocketIo(host, port))
}

object SocketIoProvider {
  def apply(host: String, port: Int): SocketIoProvider = new SocketIoProvider(host, port)
}