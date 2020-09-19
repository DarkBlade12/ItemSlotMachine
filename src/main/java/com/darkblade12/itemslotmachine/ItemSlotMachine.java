package com.darkblade12.itemslotmachine;

import com.darkblade12.itemslotmachine.coin.CoinManager;
import com.darkblade12.itemslotmachine.command.CoinCommandHandler;
import com.darkblade12.itemslotmachine.command.DesignCommandHandler;
import com.darkblade12.itemslotmachine.command.SlotCommandHandler;
import com.darkblade12.itemslotmachine.command.StatisticCommandHandler;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.plugin.PluginBase;
import com.darkblade12.itemslotmachine.plugin.hook.VaultHook;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import com.darkblade12.itemslotmachine.statistic.StatisticManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Locale;

public final class ItemSlotMachine extends PluginBase {
    private final Settings settings;
    private final VaultHook vaultHook;

    public ItemSlotMachine() {
        super(49751, 7232, Locale.US, Locale.GERMANY);
        settings = new Settings(this);
        vaultHook = new VaultHook(this);

        registerCommandHandler(new DesignCommandHandler(this));
        registerCommandHandler(new CoinCommandHandler(this));
        registerCommandHandler(new SlotCommandHandler(this));
        registerCommandHandler(new StatisticCommandHandler(this));

        registerManager(new DesignManager(this));
        registerManager(new CoinManager(this));
        registerManager(new SlotMachineManager(this));
        registerManager(new StatisticManager(this));
    }

    @Override
    public boolean load() {
        try {
            settings.load();
        } catch (Exception e) {
            logException(e, "An error occurred while loading the settings from config.yml!");
            return false;
        }

        try {
            if (!loadTemplate()) {
                return false;
            }
        } catch (Exception e) {
            logException(e, "An error occurred while loading the template file!");
            return false;
        }

        if (vaultHook.enable()) {
            logInfo("Vault hooked, money distribution is active.");
        }

        return true;
    }

    @Override
    public void unload() {
    }

    @Override
    public boolean reload() {
        try {
            settings.reload();
        } catch (Exception e) {
            logException(e, "An error occurred while loading the settings from config.yml!");
            return false;
        }

        return false;
    }

    private boolean loadTemplate() {
        File template = new File(getDataFolder(), SlotMachine.TEMPLATE_FILE);
        if (!template.exists()) {
            saveResource(SlotMachine.TEMPLATE_FILE, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(template);
        return !config.getKeys(false).isEmpty();
    }

    @Override
    public boolean isDebugEnabled() {
        return settings.isDebugModeEnabled();
    }

    @Override
    public Locale getCurrentLocale() {
        return Locale.forLanguageTag(settings.getLanguageTag());
    }

    public Settings getSettings() {
        return settings;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }
}
