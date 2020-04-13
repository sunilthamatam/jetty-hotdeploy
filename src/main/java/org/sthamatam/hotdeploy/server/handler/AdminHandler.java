package org.sthamatam.hotdeploy.server.handler;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.sthamatam.hotdeploy.server.RollingServer;
import org.sthamatam.hotdeploy.server.thread.PausableQueuedThreadPool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sunilthamatam
 */
public class AdminHandler extends AbstractHandler {

	private static final Logger LOG = Log.getLogger(AdminHandler.class);

	final static String URI_SERVER = "server";
	final static String URI_STATUS_PAUSE = "pause";
	final static String URI_STATUS_RESUME = "resume";
	final static String URI_STATUS_STOP = "stop";

	public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

		baseRequest.setHandled(true);

		String method = request.getMethod();
		if (HttpMethod.POST.is(method)) {

			String [] splits = target.split("/");
			if (splits.length >= 3) {
				if (URI_SERVER.equals(splits[1])) {

					RollingServer context = RollingServer.getInstance();
					PausableQueuedThreadPool pool = (PausableQueuedThreadPool) context.getServer().getThreadPool();
					switch (splits[2]) {

						case URI_STATUS_PAUSE : pool.pause();
							LOG.info("Server paused");
							break;

						case URI_STATUS_RESUME : pool.resume();
							LOG.info("Server resumed");
							break;

						case URI_STATUS_STOP :
							LOG.info("Shutdown in progress");

							pool.pause();
							context.stop();

							break;
					}
				}
			}
		}
	}
}
