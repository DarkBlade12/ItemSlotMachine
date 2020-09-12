package com.darkblade12.itemslotmachine.plugin.settings;

import org.bukkit.configuration.file.FileConfiguration;

import com.darkblade12.itemslotmachine.plugin.PluginBase;

public abstract class SettingsBase<T extends PluginBase> {
    protected T plugin;
    protected FileConfiguration config;

    protected SettingsBase(T plugin) {
        this.plugin = plugin;
    }

    public abstract void load();
    
    public abstract void unload();
    
    public void reload() {
        unload();
        load();
    }
}
