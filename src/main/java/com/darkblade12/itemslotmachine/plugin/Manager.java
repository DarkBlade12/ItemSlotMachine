package com.darkblade12.itemslotmachine.plugin;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;

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

    public void enable() {
        onEnable();
        registerEvents();
    }

    public void disable() {
        unregisterEvents();
        onDisable();
    }

    public void reload() {
        unregisterEvents();
        onReload();
        registerEvents();
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    protected void onReload() {
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
