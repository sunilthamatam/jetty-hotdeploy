package org.sthamatam.server.handler;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.sthamatam.server.deploy.JarTask;
import org.sthamatam.server.deploy.Task;
import org.sthamatam.server.deploy.WarTask;
import org.sthamatam.server.deploy.ZipTask;

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
					   HttpServletResponse response) throws IOException, ServletException {

		baseRequest.setHandled(true);

		String method = request.getMethod();
		if (HttpMethod.POST.is(method)) {
			String path = request.getParameter("context");
			String file = request.getParameter("file");

			Task task = getTask(path, file);
			try {
				task.execute();
				response.setStatus(HttpServletResponse.SC_OK);

				// perform gc
				System.gc();

			} catch (RuntimeException e) {
				// TODO - add error logging
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

	public Task getTask(String path, String file) {

		return file.endsWith(".war") ?
					new WarTask(file, path) :
					file.endsWith(".zip") ?
							new ZipTask(file, path) :
							new JarTask(file, path);
	}
}
