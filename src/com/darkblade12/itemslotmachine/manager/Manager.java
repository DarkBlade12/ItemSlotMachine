package com.darkblade12.itemslotmachine.manager;

import java.util.Random;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public abstract class Manager implements Listener {
	protected static final Random RANDOM = new Random();
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

	protected void registerListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected void unregisterListener() {
		HandlerList.unregisterAll(this);
	}
}