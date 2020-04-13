package org.sthamatam.server.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author sunilthamatam
 */
public class OkHandler extends AbstractHandler {

	private static final Logger LOG = Log.getLogger(OkHandler.class);

	private String message;

	public void handle(String target, Request baseRequest,
					   HttpServletRequest request,
					   HttpServletResponse response) throws IOException, ServletException {
		LOG.info("Generic handler : {}", message);

		response.setStatus(HttpServletResponse.SC_OK);
		if (message != null && target.equals("/")) {
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.write(message);

				response.setContentType("text/html");
				response.setContentLength(message.length());

			} catch (IOException e) {
				LOG.info(e);
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}

	public void setMessage(String msg) {
		message = msg;
	}
}
