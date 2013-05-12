package controllers

import javax.inject.Inject
import model.CallModel
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result
import uk.co.unclealex.callerid.remote.call.ReceivedCallsService
import uk.co.unclealex.callerid.remote.number.NumberFormatter
import uk.co.unclealex.callerid.remote.model.CallerIdSchema._
import uk.co.unclealex.callerid.remote.model.CallerIdSchema
import org.pac4j.play.scala.ScalaController
import org.pac4j.play.CallbackController
import play.mvc.Http.Context

class Application @Inject() (
  /**
   * The number formatter used to format telephone numbers.
   */
  numberFormatter: NumberFormatter,
  /**
   * The received calls service used to list all received calls.
   */
  receivedCallsService: ReceivedCallsService) extends ScalaController {

  def index = RequiresAuthentication("Google2Client") {
    profile =>
      Transactional {
        Action {
          val allCallModels = receivedCallsService.calls.map(
            rc => {
              val pn = rc.phoneNumber
              CallModel(rc, numberFormatter.formatNumberAsInternational(pn), numberFormatter.formatAddress(pn))
            })
          Ok(views.html.index(allCallModels, profile.getDisplayName()))
        }
      }
  }

  case class Transactional[A](action: Action[A]) extends Action[A] {

    def apply(request: Request[A]): Result = inTransaction {
      action(request)
    }

    lazy val parser = action.parser
  }
}

