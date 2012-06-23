package uk.co.unclealex.callerid.server.service.modem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class NetworkModemProvider implements ModemProvider {

	private InputStream i_inputStream;
	private OutputStream i_outputStream;
	private Socket i_socket;
	private String i_host;
	private int i_port;
	
	@PostConstruct
	public void initialise() throws IOException {
		Socket socket = new Socket(getHost(), getPort());
		setSocket(socket);
		setInputStream(socket.getInputStream());
		setOutputStream(socket.getOutputStream());
	}

	@PreDestroy
	public void close() throws IOException {
		getSocket().close();
	}
	
	public InputStream getInputStream() {
		return i_inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		i_inputStream = inputStream;
	}

	public OutputStream getOutputStream() {
		return i_outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		i_outputStream = outputStream;
	}

	public String getHost() {
		return i_host;
	}

	public void setHost(String host) {
		i_host = host;
	}

	public int getPort() {
		return i_port;
	}

	public void setPort(int port) {
		i_port = port;
	}

	public Socket getSocket() {
		return i_socket;
	}

	public void setSocket(Socket socket) {
		i_socket = socket;
	}
}
