package org.sthamatam.hotdeploy.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.sthamatam.hotdeploy.server.handler.AdminHandler;
import org.sthamatam.hotdeploy.server.handler.DeployHandler;
import org.sthamatam.hotdeploy.server.thread.PausableQueuedThreadPool;

import java.util.Map;

/**
 * @author sunilthamatam
 */
public abstract class RollingServer {

	protected static RollingServer _instance;

	public static RollingServer getInstance() {
		return _instance;
	}

	public abstract Server getServer();
	public abstract Map<String, Handler> getAppMap();
	public abstract String getServerHome();
	public abstract String getAppsHome();
	public abstract void await() throws InterruptedException;
	public abstract void stop();

	public static void main( String[] args ) throws Exception {

		PausableQueuedThreadPool pool = new PausableQueuedThreadPool();
		Server server = new Server(pool);

		// http connector
		ServerConnector http = new ServerConnector(server);
		http.setName("http-connector");
		http.setPort(8080);
		http.setIdleTimeout(5000);
		server.addConnector(http);

		// admin connector
		ServerConnector admin = new ServerConnector(server,
                                                    new QueuedThreadPool(),
                                                    null,
                                                    null,
                                                    -1,
                                                    -1,
                                                    new HttpConnectionFactory());
		admin.setName("admin");
		admin.setPort(8081);
		server.addConnector(admin);

		// SSL Connector
		/**
        SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector();
        String jetty_home =
        System.getProperty("jetty.home","../jetty-distribution/target/distribution");
        System.setProperty("jetty.home",jetty_home);
        ssl_connector.setPort(8443);
        SslContextFactory cf = ssl_connector.getSslContextFactory();
        cf.setKeyStore(jetty_home + "/etc/keystore");
        cf.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        cf.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

        server.setConnectors(new Connector[]{ connector0, connector1, ssl_connector });


        // directory listing
        ResourceHandler handler = new ResourceHandler();
        handler.setDirectoriesListed(true);
        handler.setResourceBase("/users/sunilthamatam");

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{handler, new OkHandler()});
        server.setHandler(handlerList);
        **/

		// context handler
		/**
        ContextHandler contextHandler = new ContextHandler("/logging");
        server.setHandler(contextHandler);
        **/

		// mutable handler-collection
		HandlerCollection handlerCollection = new HandlerCollection(true);
		server.setHandler(handlerCollection);

		// add root handlers
        /**
        ContextHandler rootHandler = new ContextHandler("/");
        OkHandler okHandler = new OkHandler();
        okHandler.setMessage("<h2> Welcome to Hotspot Server </h2>");
        rootHandler.setHandler(okHandler);
        // add this handler to administrative server-connector
        rootHandler.setVirtualHosts(new String[]{"@http-connector"});
        handlerCollection.addHandler(rootHandler);

        rootHandler = new ContextHandler("/");
        rootHandler.setHandler(new OkHandler());
        rootHandler.setVirtualHosts(new String[]{"@admin"});
        handlerCollection.addHandler(rootHandler);
        **/

		// add deploy handler
		ContextHandler deployHandler = new ContextHandler();
		deployHandler.setContextPath("/deploy");
		deployHandler.setHandler(new DeployHandler());
		// add this handler to administrative server-connector
		deployHandler.setVirtualHosts(new String[]{"@admin"});
		handlerCollection.addHandler(deployHandler);

		// add admin handler
		ContextHandler adminHandler = new ContextHandler();
		adminHandler.setContextPath("/admin");
		adminHandler.setHandler(new AdminHandler());
		// add this handler to administrative server-connector
		adminHandler.setVirtualHosts(new String[]{"@admin"});
		handlerCollection.addHandler(adminHandler);

		RollingServer serverContext = new RollingServerImpl(server);
		server.start();

		// fastest way to stop the server and jvm
		// server.join();
		serverContext.await();
		server.stop();

		// kill as daemon threads block the VM exit
		System.exit(0);
	}
}
