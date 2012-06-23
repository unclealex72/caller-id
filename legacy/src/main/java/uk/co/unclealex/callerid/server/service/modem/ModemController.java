package uk.co.unclealex.callerid.server.service.modem;

import java.io.IOException;

public interface ModemController {

	public void addModemResponseListener(ModemResponseListener modemResponseListener);
	public void sendCommand(String command) throws IOException;

}
