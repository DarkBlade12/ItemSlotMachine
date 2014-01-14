package com.darkblade12.itemslotmachine.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.plugin.Plugin;

public final class TemplateReader extends FileReader {
	private Plugin plugin;

	public TemplateReader(Plugin plugin, String fileName, String directoryName) {
		super(fileName, directoryName);
		this.plugin = plugin;
	}

	public TemplateReader(Plugin plugin, String resourceFileName, String fileName, String directoryName) {
		super(resourceFileName, fileName, directoryName);
		this.plugin = plugin;
	}

	public boolean readTemplate() {
		return outputFile.exists() || saveDefaultTemplate();
	}

	public boolean saveDefaultTemplate() {
		return saveResourceFile(plugin);
	}

	public boolean saveTemplate(File outputFile) {
		if (!readTemplate())
			return false;
		new File(outputPath).mkdirs();
		try {
			InputStream in = new FileInputStream(this.outputFile);
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
}