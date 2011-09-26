package uk.co.unclealex.callerid.server.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.co.unclealex.callerid.server.service.modem.Writable;

public class CommandServlet extends HttpServlet {

	private ServerSocket i_serverSocket;
	private Writable i_writable;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		Writable writable = WebApplicationContextUtils.getRequiredWebApplicationContext(servletConfig.getServletContext()).getBean(Writable.class);
		setWritable(writable);
		try {
			setServerSocket(new ServerSocket(3002));
		}
		catch (IOException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String str = req.getQueryString();
		str = URLDecoder.decode(str);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(str.length());
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(buffer, Charset.forName("ASCII")));
		writer.println(str);
		writer.flush();
		getWritable().write(buffer.toByteArray());
	}

	public ServerSocket getServerSocket() {
		return i_serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		i_serverSocket = serverSocket;
	}

	public Writable getWritable() {
		return i_writable;
	}

	public void setWritable(Writable writable) {
		i_writable = writable;
	}
}
