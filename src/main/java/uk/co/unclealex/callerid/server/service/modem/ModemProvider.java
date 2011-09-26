package uk.co.unclealex.callerid.server.service.modem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ModemProvider {

	public InputStream getInputStream() throws IOException;
	public OutputStream getOutputStream() throws IOException;
}
