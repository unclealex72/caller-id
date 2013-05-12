package controllers

import javax.inject.Inject
import model.CallModel
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result
import uk.co.unclealex.callerid.remote.call.ReceivedCallsService
import uk.co.unclealex.callerid.remote.number.NumberFormatter

class Application @Inject() (
  /**
   * The number formatter used to format telephone numbers.
   */
  numberFormatter: NumberFormatter,
  /**
   * The received calls service used to list all received calls.
   */
  receivedCallsService: ReceivedCallsService) extends Controller {

  def index = Transactional {
    Action {
      val allCallModels = receivedCallsService.calls.map(
        rc => {
          val pn = rc.phoneNumber
          CallModel(rc, numberFormatter.formatNumberAsInternational(pn), numberFormatter.formatAddress(pn))
        })
      Ok(views.html.index(allCallModels))
    }
  }

  case class Transactional[A](action: Action[A]) extends Action[A] {

    def apply(request: Request[A]): Result = {
      action(request)
    }

    lazy val parser = action.parser
  }
}

