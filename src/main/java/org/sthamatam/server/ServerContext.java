package org.sthamatam.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

import java.util.Map;

/**
 * @author sunilthamatam
 */
public abstract class ServerContext {

	protected static ServerContext _instance;

	public static ServerContext getInstance() {
		return _instance;
	}

	public abstract Server getServer();
	public abstract Map<String, Handler> getAppMap();
	public abstract String getServerHome();
	public abstract String getAppsHome();
	public abstract void await() throws InterruptedException;
	public abstract void stop();
}
