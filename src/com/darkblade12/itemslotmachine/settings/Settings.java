package com.darkblade12.itemslotmachine.settings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public final class Settings {
    private static final SimpleSection GENERAL_SETTINGS = new SimpleSection("General_Settings");
    private static final SimpleSection SLOT_MACHINE_SETTINGS = new SimpleSection("Slot_Machine_Settings");
    private static final SimpleSection SPACE_CHECK = new SimpleSection(SLOT_MACHINE_SETTINGS, "Space_Check");
    private static final SimpleSection COIN_SETTINGS = new SimpleSection(SLOT_MACHINE_SETTINGS, "Coin_Settings");
    private static final SimpleSection LIMITED_USAGE = new SimpleSection(SLOT_MACHINE_SETTINGS, "Limited_Usage");
    private static final SimpleSection DESIGN_SETTINGS = new SimpleSection("Design_Settings");
    private static final String BLOCK_LIST_FORMAT = "[\\w\\s]+(, [\\w\\s]+)*";
    private ItemSlotMachine plugin;
    private static boolean debugModeEnabled;
    private static String languageTag;
    private static String slotMachineNamePattern;
    private static boolean spaceCheckEnabled;
    private static Set<Material> spaceCheckIgnoredBlocks;
    private static Material coinMaterial;
    private static boolean coinCommonItemEnabled;
    private static double coinPrice;
    private static boolean limitedUsageEnabled;
    private static int limitedUsageAmount;
    private static String designNamePattern;

    public Settings(ItemSlotMachine plugin) {
        this.plugin = plugin;
    }

    public void load() throws InvalidValueException {
        Configuration config = plugin.getConfig();
        debugModeEnabled = GENERAL_SETTINGS.getBoolean(config, "Debug_Mode_Enabled");
        languageTag = GENERAL_SETTINGS.getString(config, "Language_Tag");
        if (languageTag == null) {
            throw new InvalidValueException("Language_Tag", GENERAL_SETTINGS, "is null");
        }

        slotMachineNamePattern = SLOT_MACHINE_SETTINGS.getString(config, "Name_Pattern");
        if (slotMachineNamePattern == null) {
            throw new InvalidValueException("Name_Pattern", SLOT_MACHINE_SETTINGS, "is null");
        } else if (!slotMachineNamePattern.contains("{0}")) {
            throw new InvalidValueException("Name_Pattern", SLOT_MACHINE_SETTINGS, "does not contain id placeholder '{0}'");
        }

        spaceCheckEnabled = SPACE_CHECK.getBoolean(config, "Enabled");
        if (spaceCheckEnabled) {
            spaceCheckIgnoredBlocks = new HashSet<Material>();
            String ignoredBlocksString = SPACE_CHECK.getString(config, "Ignored_Blocks");
            if (ignoredBlocksString != null) {
                if (!ignoredBlocksString.matches(BLOCK_LIST_FORMAT)) {
                    throw new InvalidValueException("Ignored_Blocks", SPACE_CHECK, "has an invalid format");
                }

                for (String blockMaterial : ignoredBlocksString.split(", ")) {
                    Material material = Material.matchMaterial(blockMaterial);
                    if (material == null || !material.isBlock()) {
                        throw new InvalidValueException("Ignored_Blocks", SPACE_CHECK, "contains an invalid block name");
                    }

                    spaceCheckIgnoredBlocks.add(material);
                }
            }
        }

        String coinString = COIN_SETTINGS.getString(config, "Item");
        if (coinString == null) {
            throw new InvalidValueException("Item", COIN_SETTINGS, "is null");
        }
        try {
            coinMaterial = Material.matchMaterial(coinString);
        } catch (Exception e) {
            throw new InvalidValueException("Item", COIN_SETTINGS, e.getMessage());
        }

        coinCommonItemEnabled = COIN_SETTINGS.getBoolean(config, "Common_Item_Enabled");
        coinPrice = COIN_SETTINGS.getDouble(config, "Price");
        if (coinPrice < 0) {
            throw new InvalidValueException("Price", COIN_SETTINGS, "is invalid (lower than 0)");
        }

        limitedUsageEnabled = LIMITED_USAGE.getBoolean(config, "Enabled");
        if (limitedUsageEnabled) {
            limitedUsageAmount = LIMITED_USAGE.getInt(config, "Amount");
            if (limitedUsageAmount < 1) {
                throw new InvalidValueException("Amount", LIMITED_USAGE, "is invalid (lower than 1)");
            }
        }

        designNamePattern = DESIGN_SETTINGS.getString(config, "Name_Pattern");
        if (designNamePattern == null) {
            throw new InvalidValueException("Name_Pattern", DESIGN_SETTINGS, "is null");
        } else if (!designNamePattern.contains("{0}")) {
            throw new InvalidValueException("Name_Pattern", DESIGN_SETTINGS, "does not contain id placeholder '{0}'");
        }
    }

    public void reload() throws InvalidValueException {
        plugin.reloadConfig();
        load();
    }

    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public static String getLanguageTag() {
        return languageTag;
    }

    public static boolean isSpaceCheckEnabled() {
        return spaceCheckEnabled;
    }

    public static Set<Material> getSpaceCheckIgnoredBlocks() {
        return Collections.unmodifiableSet(spaceCheckIgnoredBlocks);
    }

    public static boolean isBlockIgnored(Material material) {
        return spaceCheckIgnoredBlocks.contains(material);
    }

    public static Material getCoinMaterial() {
        return coinMaterial;
    }

    public static String getSlotMachineNamePattern() {
        return slotMachineNamePattern;
    }

    public static boolean isCommonCoinItemEnabled() {
        return coinCommonItemEnabled;
    }

    public static double getCoinPrice() {
        return coinPrice;
    }

    public static boolean isLimitedUsageEnabled() {
        return limitedUsageEnabled;
    }

    public static int getLimitedUsageAmount() {
        return limitedUsageAmount;
    }

    public static String getDesignNamePattern() {
        return designNamePattern;
    }
}