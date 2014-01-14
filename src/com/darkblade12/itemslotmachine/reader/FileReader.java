package com.darkblade12.itemslotmachine.reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.plugin.Plugin;

public abstract class FileReader {
	protected String resourceFileName;
	protected String outputFileName;
	protected String outputPath;
	protected File outputFile;

	protected FileReader(String resourceFileName, String outputFileName, String outputPath) {
		this.resourceFileName = resourceFileName;
		this.outputFileName = outputFileName;
		if (!outputPath.endsWith("/"))
			outputPath += "/";
		this.outputPath = outputPath;
		outputFile = new File(outputPath + outputFileName);
	}

	protected FileReader(String fileName, String outputPath) {
		this(fileName, fileName, outputPath);
	}

	protected void deleteFile() {
		if (outputFile.exists())
			outputFile.delete();
	}

	protected boolean saveResourceFile(Plugin plugin) {
		InputStream in = plugin.getResource(resourceFileName);
		if (in == null)
			return false;
		new File(outputPath).mkdirs();
		try {
			OutputStream out = new FileOutputStream(outputFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			in.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String getResourceFileName() {
		return this.resourceFileName;
	}

	public String getOuputFileName() {
		return this.outputFileName;
	}

	public String getOuputPath() {
		return this.outputPath;
	}

	public File getOuputFile() {
		return this.outputFile;
	}
}