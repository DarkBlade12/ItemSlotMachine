package com.darkblade12.itemslotmachine;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import com.darkblade12.itemslotmachine.coin.CoinManager;
import com.darkblade12.itemslotmachine.command.coin.CoinCommandHandler;
import com.darkblade12.itemslotmachine.command.design.DesignCommandHandler;
import com.darkblade12.itemslotmachine.command.slot.SlotCommandHandler;
import com.darkblade12.itemslotmachine.command.statistic.StatisticCommandHandler;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.hook.VaultHook;
import com.darkblade12.itemslotmachine.message.MessageManager;
import com.darkblade12.itemslotmachine.metrics.MetricsLite;
import com.darkblade12.itemslotmachine.reader.TemplateReader;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import com.darkblade12.itemslotmachine.statistic.StatisticManager;

public final class ItemSlotMachine extends JavaPlugin {
    public static final String MASTER_PERMISSION = "ItemSlotMachine.*";
    private Logger logger;
    private Settings settings;
    private VaultHook vaultHook;
    public TemplateReader template;
    public MessageManager messageManager;
    public DesignManager designManager;
    public CoinManager coinManager;
    public SlotMachineManager slotMachineManager;
    public StatisticManager statisticManager;
    public DesignCommandHandler designCommandHandler;
    public CoinCommandHandler coinCommandHandler;
    public SlotCommandHandler slotCommandHandler;
    public StatisticCommandHandler statisticCommandHandler;

    @Override
    public void onEnable() {
        long check = System.currentTimeMillis();
        logger = getLogger();

        settings = new Settings(this);
        try {
            settings.load();
        } catch (Exception e) {
            logWarning("An error occurred while loading the settings from config.yml, plugin will disable! Cause: %c", e);

            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }

            disable();
            return;
        }

        template = new TemplateReader(this, "template.yml", "plugins/ItemSlotMachine/");
        if (!template.readTemplate()) {
            logWarning("Failed to read template.yml, plugin will disable!");
            disable();
            return;
        }

        vaultHook = new VaultHook();
        if (vaultHook.enable()) {
            logger.info("Vault hooked, money distribution is active.");
        }

        messageManager = new MessageManager(this);
        if (!messageManager.onInitialize()) {
            return;
        }
        designManager = new DesignManager(this);
        coinManager = new CoinManager(this);
        slotMachineManager = new SlotMachineManager(this);
        statisticManager = new StatisticManager(this);
        designCommandHandler = new DesignCommandHandler(this);
        coinCommandHandler = new CoinCommandHandler(this);
        slotCommandHandler = new SlotCommandHandler(this);
        statisticCommandHandler = new StatisticCommandHandler(this);

        enableMetrics();
        check = System.currentTimeMillis() - check;
        logInfo("Gambling system version " + getDescription().getVersion() + " activated! Have fun ;D (" + check + " ms)");
    }

    @Override
    public void onDisable() {
        if (slotMachineManager != null) {
            slotMachineManager.onDisable();
        }

        logger.info("Gambling system deactivated!");
    }

    public void onReload() {
        try {
            settings.reload();
        } catch (Exception e) {
            logWarning("An error occurred while loading the settings from config.yml, plugin will disable! Cause: <cause>", e);

            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!messageManager.onReload()) {
            return;
        }
        designManager.onReload();
        coinManager.onReload();
        slotMachineManager.onReload();
    }

    public Configuration loadConfig() {
        if (new File("plugins/" + getName() + "/config.yml").exists()) {
            logInfo("config.yml successfully loaded.");
        } else {
            saveDefaultConfig();
        }
        
        return getConfig();
    }

    public void enableMetrics() {
        try {
            MetricsLite metrics = new MetricsLite(this);
            if (!metrics.isEnabled()) {
                logWarning("Metrics is disabled!");
            } else {
                logInfo("This plugin is using Metrics by BtoBastian!");
            }
        } catch (Exception e) {
            logWarning("An error occurred while enabling Metrics! Cause: %c", e);
            
            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }
        }
    }

    public void logWarning(String message, Exception exception) {
        String cause = "Unknown";

        if (exception != null && exception.getMessage() != null) {
            cause = exception.getMessage();
        }

        logger.warning(message.replace("%c", cause));
    }

    public void logWarning(String message) {
        logWarning(message, null);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    private void disable() {
        getServer().getPluginManager().disablePlugin(this);
    }
}