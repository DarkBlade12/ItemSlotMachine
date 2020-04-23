package com.darkblade12.itemslotmachine.core.hook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.darkblade12.itemslotmachine.core.PluginBase;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public final class VaultHook extends Hook<PluginBase> {
    public static final String DEFAULT_GROUP = "Default";
    private Economy economy;
    private Permission permission;

    public VaultHook(PluginBase core) {
        super(core, "Vault");
    }

    @Override
    protected boolean initialize() {
        RegisteredServiceProvider<Economy> economyRsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyRsp != null) {
            economy = economyRsp.getProvider();
        }

        RegisteredServiceProvider<Permission> permissionRsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionRsp != null) {
            permission = permissionRsp.getProvider();
        }

        return economy != null || permission != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean isEconomyEnabled() {
        return economy != null && economy.isEnabled();
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isPermissionEnabled() {
        return permission != null && permission.isEnabled();
    }

    public double getBalance(OfflinePlayer player) {
        return !isEconomyEnabled() ? 0 : economy.getBalance(player);
    }

    public boolean withdrawPlayer(OfflinePlayer player, double amount) {
        if (!isEconomyEnabled()) {
            return false;
        }

        EconomyResponse resp = economy.withdrawPlayer(player, amount);
        return resp.transactionSuccess();
    }

    public boolean depositPlayer(OfflinePlayer player, double amount) {
        if (!isEconomyEnabled()) {
            return false;
        }

        EconomyResponse resp = economy.depositPlayer(player, amount);
        return resp.transactionSuccess();
    }

    public String getCurrencyName(boolean singular, boolean spaced) {
        if (!isEconomyEnabled()) {
            return "";
        }

        String name = singular ? economy.currencyNameSingular() : economy.currencyNamePlural();
        if (spaced && name.length() > 0) {
            name = " " + name;
        }
        return name;
    }

    public String getCurrencyName(double amount, boolean spaced) {
        return getCurrencyName(amount == 1, spaced);
    }

    public String getCurrencyName(boolean singular) {
        return getCurrencyName(singular, false);
    }

    public String getCurrencyName(double amount) {
        return getCurrencyName(amount, false);
    }

    public String getPrimaryGroup(Player player) {
        if (!isPermissionEnabled()) {
            return DEFAULT_GROUP;
        }

        try {
            return permission.getPrimaryGroup(player);
        } catch (Exception ex) {
            if (base.isDebugEnabled()) {
                ex.printStackTrace();
            }
            return DEFAULT_GROUP;
        }
    }
}