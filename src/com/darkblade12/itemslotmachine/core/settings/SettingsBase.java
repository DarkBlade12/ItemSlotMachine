package com.darkblade12.itemslotmachine.core.settings;

import org.bukkit.configuration.file.FileConfiguration;

import com.darkblade12.itemslotmachine.core.PluginBase;

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
