package com.darkblade12.itemslotmachine;

import java.util.Locale;

import com.darkblade12.itemslotmachine.coin.CoinManager;
import com.darkblade12.itemslotmachine.core.PluginBase;
import com.darkblade12.itemslotmachine.core.command.CommandRegistrationException;
import com.darkblade12.itemslotmachine.core.command.coin.CoinCommandHandler;
import com.darkblade12.itemslotmachine.core.command.design.DesignCommandHandler;
import com.darkblade12.itemslotmachine.core.command.slot.SlotCommandHandler;
import com.darkblade12.itemslotmachine.core.command.statistic.StatisticCommandHandler;
import com.darkblade12.itemslotmachine.core.hook.VaultHook;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.reader.TemplateReader;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import com.darkblade12.itemslotmachine.statistic.StatisticManager;

public final class ItemSlotMachine extends PluginBase {
    private final Settings settings;
    private final VaultHook vaultHook;
    public TemplateReader template;
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
        } catch (Exception e) {
            logException("An error occurred while loading the settings from config.yml: %c", e);
            disable();
            return;
        }

        template = new TemplateReader(this, "template.yml", "plugins/ItemSlotMachine/");
        if (!template.readTemplate()) {
            logWarning("Failed to read template.yml!");
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
            logException("Failed to register commands: %c", ex);
        }

        try {
            messageManager.onEnable();
            designManager.onEnable();
            coinManager.onEnable();
            slotMachineManager.onEnable();
            statisticManager.onEnable();
        } catch (Exception ex) {
            logException("Failed to enable managers: %c", ex);
        }

        enableMetrics();
        long duration = System.currentTimeMillis() - startTime;
        logInfo("Version " + getDescription().getVersion() + " loaded. (" + duration + " ms)");
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        if (slotMachineManager != null) {
            slotMachineManager.onDisable();
        }

        logInfo("Version " + getDescription().getVersion() + " disabled.");
    }

    @Override
    public boolean onReload() {
        try {
            settings.reload();
        } catch (Exception ex) {
            logException("An error occurred while loading the settings from config.yml: %c", ex);
            disable();
            return false;
        }

        try {
            messageManager.onReload();
            designManager.onReload();
            coinManager.onReload();
            slotMachineManager.onReload();
        } catch (Exception ex) {
            logException("Failed to reload managers: %c", ex);
            disable();
            return false;
        }

        return true;
    }

    @Override
    public boolean isDebugEnabled() {
        return Settings.isDebugModeEnabled();
    }

    @Override
    public Locale getCurrentLanguage() {
        return Locale.forLanguageTag(Settings.getLanguageTag());
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }
}