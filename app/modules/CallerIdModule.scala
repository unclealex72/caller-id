package modules

import actors.{CallLoggingActor, ModemActor, ModemLoggingActor, SqueezeboxActor}
import call.{ReceivedCallFormatterImpl, ReceivedCallFormatter, ReceivedCallFactory, ReceivedCallFactoryImpl}
import contact.{ContactServiceImpl, ContactService, PersistedContactService, SlickPersistedContactService}
import device.SocketIoProvider
import modem.{AtzModem, Modem, ModemListener}
import number._
import scaldi.Module
import squeezebox.{Squeezebox, SqueezeboxImpl}
import time.NowService

/**
 * Created by alex on 06/09/15.
 */
class CallerIdModule extends Module {

  // Present call information
  bind[ModemActor] to injected[ModemActor]
  bind[ModemLoggingActor] to injected[ModemLoggingActor]
  bind[CallLoggingActor] to injected[CallLoggingActor]
  bind[SqueezeboxActor] to injected[SqueezeboxActor]
  bind[Squeezebox] to new SqueezeboxImpl(SocketIoProvider("localhost", 9090))
  // Listen to a modem
  bind[Modem] toNonLazy new AtzModem(SocketIoProvider("localhost", 3002).get) destroyWith (_.close())
  bind[ModemListener] toNonLazy injected[ModemListener] initWith(_.run)
  // Parse telephone numbers
  bind[LocationConfiguration] to LocationConfiguration("44", "1256")
  bind[NumberLocationService] to injected[NumberLocationServiceImpl]
  bind[CityDao] to new CityDaoImpl(Countries().countries)
  bind[NumberFormatter] to injected[NumberFormatterImpl]
  // Phone calls
  bind[ReceivedCallFactory] to injected[ReceivedCallFactoryImpl]
  bind[ReceivedCallFormatter] to injected[ReceivedCallFormatterImpl]
  // Time
  bind[NowService] to NowService()
  // Contacts
  bind[PersistedContactService] to injected[SlickPersistedContactService]
  bind[ContactService] to injected[ContactServiceImpl]
}

