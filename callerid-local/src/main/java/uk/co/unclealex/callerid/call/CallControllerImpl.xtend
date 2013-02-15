package uk.co.unclealex.callerid.call

import uk.co.unclealex.callerid.call.CallController
import uk.co.unclealex.callerid.call.CallAlerter
import uk.co.unclealex.callerid.squeezebox.Squeezebox
import javax.inject.Inject

/**
 * The default implementation of {@link CallController}
 */
class CallControllerImpl implements CallController {
    
    /**
     * The {@link CallAlerter} used to alert the server that a call has been made.
     */
    @Property val CallAlerter callAlerter;
    
    /**
     * The {@link Squeezebox} object used to display text on the squeezebox.
     */
    @Property val Squeezebox squeezebox;
    
    @Inject
    public new(CallAlerter callAlerter, Squeezebox squeezebox) {
        _callAlerter = callAlerter;
        _squeezebox = squeezebox;
    }
    
    override onCall(String number) {
        val String message = callAlerter.callMade(number);
        squeezebox.displayText(message, "");
    }
    
}