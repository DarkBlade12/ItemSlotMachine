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
			byte[] buffer = new byte[1024];
			for (int length = in.read(buffer); length > 0; length = in.read(buffer))
				out.write(buffer, 0, length);
			out.close();
			in.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}