package com.darkblade12.itemslotmachine.reader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class ConfigReader extends FileReader {
	private Plugin plugin;
	private TemplateReader template;
	public YamlConfiguration config;

	public ConfigReader(Plugin plugin, String fileName, String directoryName) {
		super(fileName, directoryName);
		this.plugin = plugin;
	}

	public ConfigReader(Plugin plugin, String resourceFileName, String fileName, String directoryName) {
		super(resourceFileName, fileName, directoryName);
		this.plugin = plugin;
	}

	public ConfigReader(Plugin plugin, TemplateReader template, String fileName, String directoryName) {
		super(fileName, directoryName);
		this.plugin = plugin;
		this.template = template;
	}

	public boolean readConfig() {
		if (!outputFile.exists() && (template == null ? !saveDefaultConfig() : !template.saveTemplate(outputFile)))
			return false;
		config = YamlConfiguration.loadConfiguration(outputFile);
		return config != null;
	}

	public boolean saveConfig() {
		try {
			config.save(outputFile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean saveDefaultConfig() {
		return saveResourceFile(plugin);
	}

	public void deleteConfig() {
		deleteFile();
	}

	public TemplateReader getTemplate() {
		return this.template;
	}
}