package com.darkblade12.itemslotmachine.manager;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public abstract class Manager implements Listener {
	protected ItemSlotMachine plugin;

	public Manager(ItemSlotMachine plugin) {
		this.plugin = plugin;
	}

	public abstract boolean onInitialize();

	public abstract void onDisable();

	public boolean onReload() {
		onDisable();
		return onInitialize();
	}

	protected final void registerEvents() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected final void unregisterAll() {
		HandlerList.unregisterAll(this);
	}
}