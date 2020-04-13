package org.sthamatam.hotdeploy.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Server structure should follow below hierarchy
 *
 *	 server_home/server.jar
 * 				/lib/*.jar
 * 				/apps/*.war
 * 					 /*.jar
 * 					 /lib/*.jar
 *	 			/logs/*.log
 * 				/conf/*.json
 *
 * @author sunilthamatam
 */
public class RollingServerImpl extends RollingServer {

	public static final String SERVER_APPS = "apps";

	private Server _server;
	private ConcurrentMap<String, Handler> appMap;
	private String serverHome;
	private String appsHome;


	RollingServerImpl(Server server) {

		if (_instance != null) {
			throw new IllegalStateException("Server context already created.");
		}

		_server = server;
		appMap = new ConcurrentHashMap<String, Handler>();

		URL jar = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String jarPath = jar.getPath();
		serverHome = jarPath.substring(0, jarPath.lastIndexOf(File.separatorChar) + 1);
		appsHome = serverHome + SERVER_APPS + File.separator;

		_instance = this;
	}

	@Override
	public void await() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	@Override
	public void stop() {
		synchronized (this) {
			notify();
		}
	}

	@Override
	public Server getServer() {
		return _server;
	}

	@Override
	public Map<String, Handler> getAppMap() {
		return appMap;
	}

	@Override
	public String getServerHome() {
		return serverHome;
	}

	@Override
	public String getAppsHome() {
		return appsHome;
	}
}
