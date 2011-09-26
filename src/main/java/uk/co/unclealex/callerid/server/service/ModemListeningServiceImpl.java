package uk.co.unclealex.callerid.server.service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import uk.co.unclealex.callerid.server.service.modem.ModemController;
import uk.co.unclealex.callerid.server.service.modem.ModemResponseListener;

public class ModemListeningServiceImpl implements ModemListeningService, ModemResponseListener {

	static final Pattern NUMBER_PATTERN = Pattern.compile("NMBR = (P|[0-9]+)");

	private ModemController i_modemController;
	private NumberService i_numberService;
	
	@Override
	@PostConstruct
	public void listen() throws IOException {
		ModemController modemController = getModemController();
		modemController.addModemResponseListener(this);
		modemController.sendCommand("ATZ");
		modemController.sendCommand("AT+FCLASS=1.0");
		modemController.sendCommand("AT+VCID=1");
	}

	@Override
	public void onResponse(String line) throws Exception {
		if ("RING".equals(line)) {
			onRing();
		}
		else {
			Matcher matcher = NUMBER_PATTERN.matcher(line);
			if (matcher.matches()) {
				String number = matcher.group(1);
				if ("P".equals(number)) {
					number = null;
				}
				onNumber(number);
			}
		}
	}
	
	protected void onNumber(String number) throws Exception {
		getNumberService().onNumber(number);
	}

	protected void onRing() throws Exception {
		getNumberService().onRing();
	}

	public ModemController getModemController() {
		return i_modemController;
	}

	public void setModemController(ModemController modemController) {
		i_modemController = modemController;
	}

	public NumberService getNumberService() {
		return i_numberService;
	}

	public void setNumberService(NumberService numberService) {
		i_numberService = numberService;
	}

}
