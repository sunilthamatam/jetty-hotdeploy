package org.sthamatam.hotdeploy.server.handler;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.sthamatam.hotdeploy.server.deploy.JarTask;
import org.sthamatam.hotdeploy.server.deploy.Task;
import org.sthamatam.hotdeploy.server.deploy.WarTask;
import org.sthamatam.hotdeploy.server.deploy.ZipTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sunilthamatam
 */
public class DeployHandler extends AbstractHandler {

	public void handle(String target,
					   Request baseRequest,
					   HttpServletRequest request,
					   HttpServletResponse response) {

		baseRequest.setHandled(true);

		String method = request.getMethod();
		if (HttpMethod.POST.is(method)) {
			String context = request.getParameter("context");
			String file = request.getParameter("file");

			Task task = getTask(file, context);
			try {
				task.execute(file, context);
				response.setStatus(HttpServletResponse.SC_OK);

			} catch (RuntimeException e) {
				// TODO - add error logging
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

	public Task getTask(String file, String context) {

		return file.endsWith(".war") ?
					new WarTask(file, context) :
					file.endsWith(".zip") ?
							new ZipTask(file, context) :
							new JarTask(file, context);
	}
}
