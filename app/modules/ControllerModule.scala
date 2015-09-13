package modules

import controllers.{ContactsController, ModemResponseController}
import scaldi.Module

/**
 * Created by alex on 12/09/15.
 */
class ControllerModule extends Module {

  bind [ModemResponseController] to injected[ModemResponseController]
  bind [ContactsController] to injected[ContactsController]
}
