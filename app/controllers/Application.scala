package controllers

import java.util.Date
import javax.inject.Inject
import model.CallModel
import play.api.mvc.Action
import play.api.mvc.Controller
import uk.co.unclealex.callerid.remote.call.ReceivedCall
import uk.co.unclealex.callerid.remote.number.LocationConfiguration
import uk.co.unclealex.callerid.remote.number.NumberFormatterImpl
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

  def index = Action {
    val allCallModels = receivedCallsService.calls.map(
      rc => {
        val pn = rc.phoneNumber
        CallModel(rc, numberFormatter.formatNumberAsInternational(pn), numberFormatter.formatAddress(pn))
      })
    Ok(views.html.index(allCallModels))
  }

  def at(formattedDate: String): Date = {
    null
  }

}