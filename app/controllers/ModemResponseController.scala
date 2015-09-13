package controllers

import modem.{Number, ModemListener}
import play.api.mvc.{Action, Controller}

/**
 * Created by alex on 12/09/15.
 */
class ModemResponseController(modemListener: ModemListener) extends Controller {

  def push = Action(parse.text) { implicit request =>
    modemListener.pushResponse(Number(request.body))
    Created("")
  }
}
