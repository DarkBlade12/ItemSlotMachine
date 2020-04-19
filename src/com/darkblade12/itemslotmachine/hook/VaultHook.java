package com.darkblade12.itemslotmachine.hook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public final class VaultHook extends Hook {
    public static Economy ECONOMY;
    public static Permission PERMISSION;

    @Override
    protected boolean initialize() {
        RegisteredServiceProvider<Economy> economyRsp = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (economyRsp != null) {
            ECONOMY = economyRsp.getProvider();
        }

        RegisteredServiceProvider<Permission> permissionRsp = Bukkit.getServicesManager().getRegistration(Permission.class);

        if (permissionRsp != null) {
            PERMISSION = permissionRsp.getProvider();
        }

        return ECONOMY != null || PERMISSION != null;
    }

    public static double getBalance(OfflinePlayer player) {
        return ECONOMY == null ? 0 : ECONOMY.getBalance(player);
    }

    public static double getBalance(Player player) {
        return getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public static String getGroup(Player player) {
        try {
            return PERMISSION == null ? "Default" : PERMISSION.getPrimaryGroup(player);
        } catch (Exception e) {
            return "Default";
        }
    }

    @Override
    public String getPluginName() {
        return "Vault";
    }
}