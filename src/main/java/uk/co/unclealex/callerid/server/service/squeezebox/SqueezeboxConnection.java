package uk.co.unclealex.callerid.server.service.squeezebox;

import java.io.IOException;

public interface SqueezeboxConnection {

	public String execute(String command) throws IOException;

	public void close() throws IOException;

}
