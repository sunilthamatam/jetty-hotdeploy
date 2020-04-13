package org.sthamatam.hotdeploy.server.deploy;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.sthamatam.hotdeploy.server.RollingServer;

import java.util.Map;

/**
 * @author sunilthamatam
 */
public class WarTask extends Task {

	public WarTask(String war, String context) {
		super(war, context);
	}

	@Override
	public void execute(String pathToFile, String contextRoot) {

		try {
			RollingServer serverContext = RollingServer.getInstance();
			WebAppContext war = new WebAppContext();
			war.setContextPath(getContextRoot());
			war.setWar(serverContext.getAppsHome() + getPathToFile());
			getHandlerCollection().addHandler(war);
			war.setInitParameter("dirAllowed", "false");
			war.start();

			Map<String, Handler> appmap = RollingServer.getInstance().getAppMap();
			Handler oldApp = appmap.get(getContextRoot());
			if (oldApp != null) {
				getThreadPool().pause();
				getHandlerCollection().removeHandler(oldApp);
				getThreadPool().resume();
			}
			appmap.put(getContextRoot(), war);

		} catch (Exception e) {
			// TODO - log deployment failure
			e.printStackTrace();
		}

	}
}
