package uk.co.unclealex.callerid.server.service.squeezebox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class SqueezeboxConnectionFactoryImpl implements SqueezeboxConnectionFactory {

	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private String i_host;
	private int i_port;
	
	@Override
	public SqueezeboxConnection createSqueezeboxConnection() throws IOException {
		Socket socket = new Socket(getHost(), getPort());
		return new SqueezeboxConnectionImpl(socket);
	}

	class SqueezeboxConnectionImpl implements SqueezeboxConnection {
		
		private final Socket i_socket;

		public SqueezeboxConnectionImpl(Socket socket) {
			super();
			i_socket = socket;
		}
		
		public String execute(String command) throws IOException {
			Socket socket = getSocket();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));
			writer.println(command);
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
			String line = reader.readLine();
			if (line != null && command.endsWith("?")) {
				return line.substring(command.length() - 1);
			}
			else {
				return line;
			}
		}

		public void close() throws IOException {
			getSocket().close();
		}
		
		public Socket getSocket() {
			return i_socket;
		}
	}

	public int getPort() {
		return i_port;
	}

	public void setPort(int port) {
		i_port = port;
	}

	public String getHost() {
		return i_host;
	}

	public void setHost(String host) {
		i_host = host;
	}
}
