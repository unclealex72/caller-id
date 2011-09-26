package uk.co.unclealex.callerid.server.service.squeezebox;

import java.io.IOException;

public interface SqueezeboxConnectionFactory {

	public SqueezeboxConnection createSqueezeboxConnection() throws IOException;
}
