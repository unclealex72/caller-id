package uk.co.unclealex.callerid.server.service.listener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.web.util.UriUtils;

import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.server.service.squeezebox.SqueezeboxConnection;
import uk.co.unclealex.callerid.server.service.squeezebox.SqueezeboxConnectionFactory;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;

public class SqueezeboxDisplayingNumberListener extends AbstractOnRingNumberListener {
	
	private String i_messageToDisplay;
	private SqueezeboxConnectionFactory i_squeezeboxConnectionFactory;
	
	@Override
	protected boolean beforeFirstRing(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber)
			throws Exception {
		setMessageToDisplay(encode(phoneNumber==null?"Unknown caller":phoneNumber.toString()));
		return true;
	}
	
	@Override
	protected boolean onRing(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws IOException {
		SqueezeboxConnection conn = getSqueezeboxConnectionFactory().createSqueezeboxConnection();
		int playerCount = Integer.parseInt(conn.execute("player count ?"));
		for (int idx = 0; idx < playerCount; idx++) {
			String playerId = conn.execute(String.format("player id %d ?", idx));
			String command = String.format("%s display %s %s %d", playerId, encode("Incoming call"), getMessageToDisplay(), 15);
			conn.execute(command);
		}
		conn.close();
		return true;
	}

	protected String encode(String parameter) throws UnsupportedEncodingException {
		return UriUtils.encodeFragment(parameter, "UTF-8");
	}
	
	@Override
	protected void clearState() {
		setMessageToDisplay(null);
	}

	public String getMessageToDisplay() {
		return i_messageToDisplay;
	}

	public void setMessageToDisplay(String messageToDisplay) {
		i_messageToDisplay = messageToDisplay;
	}

	public SqueezeboxConnectionFactory getSqueezeboxConnectionFactory() {
		return i_squeezeboxConnectionFactory;
	}

	public void setSqueezeboxConnectionFactory(SqueezeboxConnectionFactory squeezeboxConnectionFactory) {
		i_squeezeboxConnectionFactory = squeezeboxConnectionFactory;
	}
}
