import java.io.{InputStream, OutputStream}

import actors.{LoggingActor, ParentActor, SqueezeboxActor}
import akka.actor.ActorSystem
import device.{Io, SocketIoProvider}
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
    bind[ActorSystem] to ActorSystem("callerId") destroyWith (_.shutdown())
    bind[ParentActor] to injected[ParentActor]
    bind[LoggingActor] to injected[LoggingActor]
    bind[SqueezeboxActor] to injected[SqueezeboxActor]
    bind[Provider[Modem]] to Provider.singleton[Modem](new AtzModem(io))
    bind[Squeezebox] to new SqueezeboxImpl(new SocketIoProvider("localhost", 9090))
    bind[ModemListener] to injected[ModemListener]
  }

  val modemListener = inject[ModemListener]
  modemListener.run()
}
