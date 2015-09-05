import actors.{CallLoggingActor, ModemActor, ModemLoggingActor, SqueezeboxActor}
import akka.actor.ActorSystem
import call.{ReceivedCallFactory, ReceivedCallFactoryImpl}
import device.SocketIoProvider
import modem.{AtzModem, Modem, ModemListener}
import number._
import scaldi.{Injectable, Module}
import squeezebox.{Squeezebox, SqueezeboxImpl}
import time.NowService
import util.Provider

/**
 * Created by alex on 01/09/15.
 */
object Main extends App with Injectable {

  implicit val injector = new Module {
    // Present call information
    bind[ActorSystem] to ActorSystem("callerId") destroyWith (_.shutdown())
    bind[ModemActor] to injected[ModemActor]
    bind[ModemLoggingActor] to injected[ModemLoggingActor]
    bind[CallLoggingActor] to injected[CallLoggingActor]
    bind[SqueezeboxActor] to injected[SqueezeboxActor]
    bind[Squeezebox] to new SqueezeboxImpl(SocketIoProvider("localhost", 3002))
    // Listen to a modem
    bind[Provider[Modem]] to Provider.singleton[Modem](new AtzModem(SocketIoProvider("localhost", 9090).get))
    bind[ModemListener] to injected[ModemListener]
    // Parse telephone numbers
    bind[LocationConfiguration] to LocationConfiguration("44", "1256")
    bind[NumberLocationService] to injected[NumberLocationServiceImpl]
    bind[CityDao] to injected[JsonResourceCityDao]
    bind[NumberFormatter] to injected[NumberFormatterImpl]
    // Phone calls
    bind[ReceivedCallFactory] to injected[ReceivedCallFactoryImpl]
    // Time
    bind[NowService] to NowService()
  }

  val modemListener = inject[ModemListener]
  modemListener.run()
}
