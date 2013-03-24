package uk.co.unclealex.callerid.modem

import javax.inject.Inject
import org.eclipse.xtend.lib.Property
import uk.co.unclealex.callerid.call.CallController
import uk.co.unclealex.callerid.device.Device
import uk.co.unclealex.process.packages.PackagesRequired

@PackagesRequired("ser2net")
class ModemListener implements Runnable {
    
    @Property val Device modemDevice;
    @Property val CallController callController;
    
    @Inject
    new(@ModemDevice Device modemDevice, CallController callController) {
        _modemDevice = modemDevice;
        _callController = callController;
    }

    /**
     * Initialise the modem and then listen for calls.
     */
    override run() {
      initialiseModem();
      listenForCalls();
    }
    
    /**
     * Send any required initilisation command strings to the modem.
     */
    def void initialiseModem() {
        #["ATZ", "AT+FCLASS=1.0", "AT+VCID=1"].forEach(
            [ command | modemDevice.writeLine(command) ]
        );
    }
        
    /**
     * Listen for any calls and then notify the {@link #callController}.
     */
    def void listenForCalls() { 
        var String line;
        while ((line = modemDevice.readLine) != null) {
            callController.onCall(line);
        }
    }

}