package com.darkblade12.itemslotmachine.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.plugin.Plugin;

public final class TextReader extends FileReader {
	private Plugin plugin;

	public TextReader(Plugin plugin, String fileName, String directoryName) {
		super(fileName, directoryName);
		this.plugin = plugin;
	}

	public boolean readFile() {
		return outputFile.exists() || saveResourceFile(plugin);
	}

	public List<String> readFromFile() throws Exception {
		if (!outputFile.exists())
			return null;
		List<String> lines = new ArrayList<String>();
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), "UTF-8"));
		for (String line = b.readLine(); line != null; line = b.readLine())
			lines.add(StringEscapeUtils.unescapeJava(line));
		b.close();
		return lines;
	}

	public boolean saveDefaultText() {
		return saveResourceFile(plugin);
	}
}