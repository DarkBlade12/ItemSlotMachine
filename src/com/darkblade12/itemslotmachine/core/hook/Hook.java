package com.darkblade12.itemslotmachine.core.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.darkblade12.itemslotmachine.core.PluginBase;

public abstract class Hook<T extends PluginBase> {
    protected T base;
    protected String pluginName;
    protected boolean enabled;

    protected Hook(T base, String pluginName) {
        this.base = base;
        this.pluginName = pluginName;
    }

    public boolean enable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(getPluginName());
        if(plugin == null || !plugin.isEnabled()) {
            return false;
        }
        
        return enabled = initialize();
    }

    protected abstract boolean initialize();

    public String getPluginName() {
        return pluginName;
    }

    public boolean isEnabled() {
        return enabled;
    }
}