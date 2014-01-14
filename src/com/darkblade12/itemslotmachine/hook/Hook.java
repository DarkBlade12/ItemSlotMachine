package com.darkblade12.itemslotmachine.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unchecked")
public abstract class Hook<P extends JavaPlugin> {
	protected P plugin;
	protected static boolean ENABLED;

	public boolean load() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(getPluginName());
		if (plugin != null) {
			this.plugin = (P) plugin;
			ENABLED = initialize();
		}
		return plugin != null && ENABLED;
	}

	protected abstract boolean initialize();

	public abstract String getPluginName();

	public P getPlugin() {
		return this.plugin;
	}

	public static boolean isEnabled() {
		return ENABLED;
	}
}