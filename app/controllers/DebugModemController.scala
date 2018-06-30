package controllers

import modem.ModemSender
import play.api.mvc.{AbstractController, Action, ControllerComponents}

class DebugModemController(val maybeModemSender: Option[ModemSender],
                           override val controllerComponents: ControllerComponents) extends AbstractController(controllerComponents) {

  def sendToModem: Action[String] = Action(parse.tolerantText(100)) { implicit request =>
    maybeModemSender match {
      case Some(modemSender) =>
        modemSender.send(request.body)
        NoContent
      case None =>
        NotFound
    }
  }
}
