package uk.co.unclealex.callerid.server.service.modem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

public class StdioModemProvider implements ModemProvider, Writable {

	private InputStream i_inputStream;
	private OutputStream i_outputStream;
	private BlockingQueue<Byte> i_bytes = new ArrayBlockingQueue<Byte>(1024);
	
	@PostConstruct
	public void initialise() throws IOException {
		setOutputStream(System.out);
		InputStream in = new InputStream() {
			@Override
			public int read() throws IOException {
				Byte by;
				try {
					by = getBytes().take();
				}
				catch (InterruptedException e) {
					throw new IOException(e);
				}
				return by.intValue();
			}
			@Override
			public int read(byte[] by) throws IOException {
				if (by.length != 0) {
					by[0] = (byte) read();
					return 1;
				}
				else {
					return 0;
				}
			}
			@Override
			public int read(byte[] by, int off, int len) throws IOException {
				if (by.length != 0) {
					by[off] = (byte) read();
					return 1;
				}
				else {
					return 0;
				}
			}
		};
		setInputStream(in);
	}

	@Override
	public void write(byte[] by) throws IOException {
		BlockingQueue<Byte> bytes = getBytes();
		for (byte b : by) {
			try {
				bytes.put(b);
			}
			catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
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


	public BlockingQueue<Byte> getBytes() {
		return i_bytes;
	}


	public void setBytes(BlockingQueue<Byte> bytes) {
		i_bytes = bytes;
	}	
}
