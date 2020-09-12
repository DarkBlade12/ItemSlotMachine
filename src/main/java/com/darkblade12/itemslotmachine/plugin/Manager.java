package com.darkblade12.itemslotmachine.plugin;

import java.io.File;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Manager<T extends PluginBase> implements Listener {
    protected final T plugin;
    protected final File dataDirectory;

    protected Manager(T plugin, File dataDirectory) {
        this.plugin = plugin;
        this.dataDirectory = dataDirectory;
    }

    protected Manager(T plugin) {
        this(plugin, null);
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public void onReload() {
        onDisable();
        onEnable();
    }

    protected final void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected final void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    public File getDataDirectory() {
        return dataDirectory;
    }
}
