package device

import util.Provider

/**
 * A provider for network io.
 * Created by alex on 31/08/15.
 */
class SocketIoProvider(host: String, port: Int) extends Provider[Io] {

  override def get: Io = new SocketIo(host, port)
}
