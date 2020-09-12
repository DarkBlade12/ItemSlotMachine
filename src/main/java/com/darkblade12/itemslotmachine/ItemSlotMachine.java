package com.darkblade12.itemslotmachine;

import java.io.File;
import java.util.Locale;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.darkblade12.itemslotmachine.coin.CoinManager;
import com.darkblade12.itemslotmachine.plugin.PluginBase;
import com.darkblade12.itemslotmachine.plugin.command.CommandRegistrationException;
import com.darkblade12.itemslotmachine.command.CoinCommandHandler;
import com.darkblade12.itemslotmachine.command.DesignCommandHandler;
import com.darkblade12.itemslotmachine.command.SlotCommandHandler;
import com.darkblade12.itemslotmachine.command.StatisticCommandHandler;
import com.darkblade12.itemslotmachine.plugin.hook.VaultHook;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import com.darkblade12.itemslotmachine.statistic.StatisticManager;

public final class ItemSlotMachine extends PluginBase {
    private final Settings settings;
    private final VaultHook vaultHook;
    public final DesignCommandHandler designCommandHandler;
    public final CoinCommandHandler coinCommandHandler;
    public final SlotCommandHandler slotCommandHandler;
    public final StatisticCommandHandler statisticCommandHandler;
    public final DesignManager designManager;
    public final CoinManager coinManager;
    public final SlotMachineManager slotMachineManager;
    public final StatisticManager statisticManager;

    public ItemSlotMachine() {
        super(49751, Locale.US, Locale.GERMANY);
        settings = new Settings(this);
        vaultHook = new VaultHook(this);

        designCommandHandler = new DesignCommandHandler(this);
        coinCommandHandler = new CoinCommandHandler(this);
        slotCommandHandler = new SlotCommandHandler(this);
        statisticCommandHandler = new StatisticCommandHandler(this);

        designManager = new DesignManager(this);
        coinManager = new CoinManager(this);
        slotMachineManager = new SlotMachineManager(this);
        statisticManager = new StatisticManager(this);
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        try {
            settings.load();
        } catch (Exception ex) {
            logException("An error occurred while loading the settings from config.yml: {0}", ex);
            disable();
            return;
        }
        
        try {
            loadTemplate();
        } catch (Exception ex) {
            logException("An error occurred while loading the template file: {0}", ex);
            disable();
            return;
        }

        if (vaultHook.enable()) {
            logInfo("Vault hooked, money distribution is active.");
        }

        try {
            designCommandHandler.enable();
            coinCommandHandler.enable();
            slotCommandHandler.enable();
            statisticCommandHandler.enable();
        } catch (CommandRegistrationException ex) {
            logException("Failed to register commands: {0}", ex);
        }

        try {
            messageManager.enable();
            designManager.enable();
            coinManager.enable();
            slotMachineManager.enable();
            statisticManager.enable();
        } catch (Exception ex) {
            logException("Failed to enable managers: {0}", ex);
        }

        enableMetrics();
        long duration = System.currentTimeMillis() - startTime;
        logInfo("Version {0} loaded. ({1} ms)", getDescription().getVersion(), duration);
        
        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                checkForUpdates();
            }
        });
    }

    @Override
    public void onDisable() {
        messageManager.disable();
        designManager.disable();
        coinManager.disable();
        slotMachineManager.disable();
        statisticManager.disable();

        logInfo("Version {0} disabled.", getDescription().getVersion());
    }

    @Override
    public boolean onReload() {
        try {
            settings.reload();
        } catch (Exception ex) {
            logException("An error occurred while loading the settings from config.yml: {0}", ex);
            disable();
            return false;
        }

        try {
            messageManager.reload();
            designManager.reload();
            coinManager.reload();
            slotMachineManager.reload();
            statisticManager.reload();
        } catch (Exception ex) {
            logException("Failed to reload managers: {0}", ex);
            disable();
            return false;
        }

        return true;
    }

    public FileConfiguration loadTemplate() {
        File template = new File(getDataFolder(), SlotMachine.TEMPLATE_FILE);
        if (!template.exists()) {
            saveResource(SlotMachine.TEMPLATE_FILE, false);
        }
        return YamlConfiguration.loadConfiguration(template);
    }

    @Override
    public boolean isDebugEnabled() {
        return settings.isDebugModeEnabled();
    }

    @Override
    public Locale getCurrentLanguage() {
        return Locale.forLanguageTag(settings.getLanguageTag());
    }

    public Settings getSettings() {
        return settings;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }
}
