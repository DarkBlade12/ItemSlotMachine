package com.darkblade12.itemslotmachine.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class Hook {
    protected static boolean ENABLED;

    public boolean enable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(getPluginName());
        ENABLED = plugin != null && plugin.isEnabled() && initialize();

        return ENABLED;
    }

    protected abstract boolean initialize();

    public abstract String getPluginName();

    public static boolean isEnabled() {
        return ENABLED;
    }
}