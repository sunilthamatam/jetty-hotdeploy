package org.sthamatam.server.deploy;

import org.sthamatam.server.ServerContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author sunilthamatam
 */
public class ZipTask extends JarTask {

	private String zipName;

	public ZipTask(String zip, String context) {
		super(null, context);
		zipName = zip;
	}

	@Override
	public void execute() {

		try {

			extractZip();

			// derive jar name to be deploye
			String partialName = zipName.substring(0, zipName.indexOf(".zip"));
			String jarName = partialName + ".jar";

			// complete jar deployment
			super.execute();

		} catch (Exception e) {
			// TODO - error log - deployment failure
			e.printStackTrace();
		}

	}

	protected void extractZip() throws FileNotFoundException, IOException {

		String appsHome = ServerContext.getInstance().getAppsHome();
		ZipFile zip = new ZipFile(appsHome + zipName);

		Enumeration entries = zip.entries();

		while( entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			InputStream is = zip.getInputStream(entry);

			// create dir or copy file to disk
			Path entryPath = Paths.get(appsHome, entry.getName());
			if (entry.isDirectory()) {
				entryPath.toFile().mkdirs();
			} else {
				Files.copy(is, entryPath,
						StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}
}
