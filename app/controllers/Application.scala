package controllers

import scala.annotation.implicitNotFound
import javax.inject.Inject
import model.CallModel
import play.api.mvc.Controller
import play.api.mvc.EssentialAction
import play.api.mvc.RequestHeader
import uk.co.unclealex.callerid.remote.call.ReceivedCallsService
import uk.co.unclealex.callerid.remote.number.NumberFormatter
import uk.co.unclealex.callerid.remote.model.CallerIdSchema.inTransaction
import com.typesafe.scalalogging.slf4j.Logging

class Application @Inject() (
  /**
   * The number formatter used to format telephone numbers.
   */
  numberFormatter: NumberFormatter,
  /**
   * The received calls service used to list all received calls.
   */
  receivedCallsService: ReceivedCallsService) extends Controller with Secured with Logging {

  def index =
    isAuthenticated { googleUser =>
      implicit request => {
        val allCallModels = inTransaction {
          receivedCallsService.calls.toList.map {
            rc =>
              val pn = rc.phoneNumber
              CallModel(rc, numberFormatter.formatNumberAsInternational(pn), numberFormatter.formatAddress(pn))
          }
        }
        Ok(views.html.index(allCallModels, googleUser.name))
      }
    }
}

