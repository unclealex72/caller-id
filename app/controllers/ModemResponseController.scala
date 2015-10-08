package controllers

import modem.{Ring, Withheld, Number, ModemListener}
import play.api.mvc.{Action, Controller}

/**
 * Created by alex on 12/09/15.
 */
class ModemResponseController(modemListener: ModemListener) extends Controller {

  def number = Action(parse.text) { implicit request =>
    modemListener.pushResponse(Number(request.body))
    Created("")
  }

  def withheld = Action { implicit request =>
    modemListener.pushResponse(Withheld)
    Created("")
  }

  def ring = Action { implicit request =>
    modemListener.pushResponse(Ring)
    Created("")
  }
}
