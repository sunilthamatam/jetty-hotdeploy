package org.sthamatam.server.deploy;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.sthamatam.server.ServerContext;
import org.sthamatam.server.thread.PausableQueuedThreadPool;

/**
 * @author sunilthamatam
 */
public abstract class Task implements Runnable {

	private Server _server;
	private String pathToFile;
	private String contextRoot;

	public Task(String pathToFile, String contextRoot) {
		this.pathToFile = pathToFile;
		this.contextRoot = contextRoot;
		_server = ServerContext.getInstance().getServer();
	}



	public abstract void execute(String pathToFile, String contextRoot);

	public void run(){
		execute(pathToFile, contextRoot);
	}

	public Server getServer() {
		return _server;
	}

	public HandlerCollection getHandlerCollection() {
		return (HandlerCollection) getServer().getHandler();
	}

	public PausableQueuedThreadPool getThreadPool() {
		return (PausableQueuedThreadPool) getServer().getThreadPool();
	}

	public String getPathToFile() {
		return pathToFile;
	}

	public String getContextRoot() {
		return contextRoot;
	}
}
