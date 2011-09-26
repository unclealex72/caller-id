package uk.co.unclealex.callerid.server.service.modem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class StreamModemController implements ModemController, Runnable {

	private static final Charset ASCII = Charset.forName("ASCII");
	private static final Logger log = LoggerFactory.getLogger(StreamModemController.class);
	
	private ModemProvider i_modemProvider;
	private List<ModemResponseListener> i_modemResponseListeners = Lists.newArrayList();
	
	@PostConstruct
	public void initialise() {
		new Thread(this).start();
	}
	
	@Override
	public void addModemResponseListener(ModemResponseListener modemResponseListener) {
		getModemResponseListeners().add(modemResponseListener);
	}

	@Override
	public void sendCommand(String command) throws IOException {
		OutputStream out = getModemProvider().getOutputStream();
		out.write(command.getBytes(ASCII));
		out.write(13);
	}

	@Override
	public void run() {
		try {
			InputStream inputStream = getModemProvider().getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, ASCII));
			String line;
			while ((line = reader.readLine()) != null) {
				for (ModemResponseListener modemResponseListener : getModemResponseListeners()) {
					try {
						modemResponseListener.onResponse(line.trim());
					}
					catch (Throwable t) {
						log.error("A modem response listener has failed.", t);
					}
				}
			}
		}
		catch (IOException e) {
			log.error("There was an issue whilst reading from the modem.", e);
		}
	}
	
	public ModemProvider getModemProvider() {
		return i_modemProvider;
	}

	public void setModemProvider(ModemProvider modemProvider) {
		i_modemProvider = modemProvider;
	}

	public List<ModemResponseListener> getModemResponseListeners() {
		return i_modemResponseListeners;
	}

	public void setModemResponseListeners(List<ModemResponseListener> modemResponseListeners) {
		i_modemResponseListeners = modemResponseListeners;
	}

}
