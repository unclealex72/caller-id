package modules

import controllers.{UserController, ContactsController, ModemResponseController}
import scaldi.Module

/**
 * Created by alex on 12/09/15.
 */
class ControllerModule extends Module {

  bind [ModemResponseController] to injected[ModemResponseController]
  bind [ContactsController] to injected[ContactsController]
  bind [UserController] to injected[UserController]
}
