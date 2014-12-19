package com.darkblade12.itemslotmachine.hook;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VaultHook extends Hook<Vault> {
	public static Economy ECONOMY;
	public static Permission PERMISSION;

	@Override
	protected boolean initialize() {
		ECONOMY = Bukkit.getServicesManager().getRegistration(Economy.class) != null ? Bukkit.getServicesManager().getRegistration(Economy.class).getProvider() : null;
		PERMISSION = Bukkit.getServicesManager().getRegistration(Permission.class) != null ? Bukkit.getServicesManager().getRegistration(Permission.class).getProvider() : null;
		return ECONOMY != null || PERMISSION != null;
	}

	@SuppressWarnings("deprecation")
	public static double getBalance(Player p) {
		return ECONOMY == null ? 0 : ECONOMY.getBalance(p.getName());
	}

	public static String getGroup(Player p) {
		try {
			return PERMISSION == null ? "Default" : PERMISSION.getPrimaryGroup(p);
		} catch (Exception e) {
			return "Default";
		}
	}

	@Override
	public String getPluginName() {
		return "Vault";
	}
}