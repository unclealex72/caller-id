package uk.co.unclealex.callerid.call

import uk.co.unclealex.callerid.squeezebox.Squeezebox

/**
 * The default implementation of {@link CallController}
 */
class CallControllerImpl(callAlerter: CallAlerter, squeezebox: Squeezebox) extends CallController {

  override def onCall(number: String) {
    val message: String = callAlerter callMade number
    squeezebox displayText (message, "")
  }

}