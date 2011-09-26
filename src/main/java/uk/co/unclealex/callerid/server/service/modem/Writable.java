package uk.co.unclealex.callerid.server.service.modem;

import java.io.IOException;

public interface Writable {

	public void write(byte[] by) throws IOException;
}
