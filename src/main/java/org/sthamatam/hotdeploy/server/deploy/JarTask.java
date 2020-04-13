package org.sthamatam.hotdeploy.server.deploy;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.sthamatam.hotdeploy.server.RollingServer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

/**
 * @author sunilthamatam
 */
public class JarTask extends Task {

	public JarTask(String jar, String context) {
		super(jar, context);
	}

	@Override
	public void execute(String pathToFile, String contextRoot) {

		try {
			Handler currentApp = createServletHandler(getPathToFile());
			getHandlerCollection().addHandler(currentApp);
			currentApp.start();

			Map<String, Handler> appmap = RollingServer.getInstance().getAppMap();
			Handler oldApp = appmap.get(getContextRoot());
			if (oldApp != null) {
				getThreadPool().pause();
				getHandlerCollection().removeHandler(oldApp);
				getThreadPool().resume();
			}
			appmap.put(getContextRoot(), currentApp);

		} catch (Exception e) {
			// TODO - error log - deployment failure
			e.printStackTrace();
		}
	}

	protected ServletContextHandler createServletHandler(String jar) {

		ServletContextHandler servletHandler = new ServletContextHandler();
		servletHandler.setContextPath(getContextRoot());

		jar = RollingServer.getInstance().getAppsHome() + jar;
		try {

			URL url = new File(jar).toURI().toURL();
			URLClassLoader appClassLoader = new URLClassLoader(new URL[]{url});

			// find manifest files
			Enumeration<URL> urls = appClassLoader.findResources("META-INF/MANIFEST.MF");
			URL manifestUrl = null;
			while (urls.hasMoreElements()) {
				URL temp = urls.nextElement();
				if(temp.getPath().contains(getPathToFile()))
					manifestUrl = temp; break;

			}

			// load classpath dependencies only when manifest attribute is found and not empty
			List<URL> urlList = new ArrayList<URL>();
			urlList.add(url);
			if (manifestUrl != null) {
				// TODO - debug log

				Manifest manifest = new Manifest(manifestUrl.openStream());
				String classpath = manifest.getMainAttributes().getValue("Class-Path");
				if (classpath != null && !classpath.equals("")) {
					String [] paths = classpath.split("\\s");
					String prefix = getJarPrefixPath(url.toString());
					for (String path : paths) {
						// TODO - debug log
						urlList.add(new URL(prefix + path));
					}
				} else {
					// TODO - info log
				}
			}
			appClassLoader = new URLClassLoader(urlList.toArray(new URL[]{}));
			servletHandler.setClassLoader(appClassLoader);

			// TODO - load servlet config from web.xml in jar
			ServletHolder servletHolder = servletHandler.addServlet("org.glassfish.jersey.servlet.ServletContainer", "/*");
			servletHolder.setInitOrder(0);
//	        servletHolder.setInitParameter("jersey.config.server.provider.classnames", );
			servletHolder.setInitParameter("jersey.config.server.provider.packages", "com.myorg.demo.rest");
			servletHolder.setInitParameter("jersey.config.server.provider.scanning.recursive", "true");

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return servletHandler;
	}

	private String getJarPrefixPath(String jar) {
		return jar.substring(0, jar.lastIndexOf('/') + 1);
	}

}
