import java.io.{InputStream, OutputStream}

import actors.{LoggingActor, ParentActor, SqueezeboxActor}
import akka.actor.ActorSystem
import call._
import device.{BufferedIoDevice, Io, SocketIoProvider}
import modem.{AtzModem, Modem, ModemListener}
import scaldi.{Injectable, Module}
import squeezebox.{Squeezebox, SqueezeboxImpl}
import util.Provider

/**
 * Created by alex on 01/09/15.
 */
object Main extends App with Injectable {

  val io: Io = new Io {
    override def in: InputStream = System.in

    override def out: OutputStream = System.out

    override def close() {}
  }

  implicit val injector = new Module {
    // Present call information
    bind[ActorSystem] to ActorSystem("callerId") destroyWith (_.shutdown())
    bind[ParentActor] to injected[ParentActor]
    bind[LoggingActor] to injected[LoggingActor]
    bind[SqueezeboxActor] to injected[SqueezeboxActor]
    bind[Squeezebox] to new SqueezeboxImpl(SocketIoProvider("localhost", 9090))
    // Listen to a modem
    bind[Provider[Modem]] to Provider.singleton[Modem](new AtzModem(BufferedIoDevice(io)))
    bind[ModemListener] to injected[ModemListener]
    // Parse telephone numbers
    bind[NumberLocationService] to injected[NumberLocationServiceImpl]
    bind[CityDao] to injected[JsonResourceCityDao]
    bind[NumberFormatter] to injected[NumberFormatterImpl]
  }

  val modemListener = inject[ModemListener]
  modemListener.run()
}
