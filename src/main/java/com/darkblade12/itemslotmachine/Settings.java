package com.darkblade12.itemslotmachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;

import com.darkblade12.itemslotmachine.core.settings.SettingsBase;

public class Settings extends SettingsBase<ItemSlotMachine> {
    private static final String DEFAULT_DESIGN_NAME_PATTERN = "design{0}";
    private static final String DEFAULT_SLOT_MACHINE_NAME_PATTERN = "design{0}";
    private static final Material DEFAULT_COIN_TYPE = Material.GOLD_NUGGET;
    private static final double DEFAULT_COIN_PRICE = 100.0;
    private boolean debugModeEnabled;
    private String languageTag;
    private String designNamePattern;
    private String slotMachineNamePattern;
    private int slotMachineUseLimit;
    private boolean spaceCheckEnabled;
    private List<Material> spaceCheckIgnoredTypes;
    private Material coinType;
    private boolean useCommonCoinItem;
    private double coinPrice;

    public Settings(ItemSlotMachine plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        config = plugin.getConfig();
        debugModeEnabled = config.getBoolean(Setting.DEBUG_MODE_ENABLED.getPath());
        languageTag = config.getString(Setting.LANGUAGE_TAG.getPath(), "en-US");
        
        designNamePattern = config.getString(Setting.DESIGN_NAME_PATTERN.getPath(), DEFAULT_DESIGN_NAME_PATTERN);
        if (!designNamePattern.contains("{0}")) {
            plugin.logInfo("Missing id placeholder in setting {0}.", Setting.DESIGN_NAME_PATTERN);
            designNamePattern = DEFAULT_DESIGN_NAME_PATTERN;
        }
        
        spaceCheckEnabled = config.getBoolean(Setting.DESIGN_SPACE_CHECK_ENABLED.getPath(), true);
        spaceCheckIgnoredTypes = new ArrayList<Material>();
        List<String> ignoredTypeNames = config.getStringList(Setting.DESIGN_SPACE_CHECK_IGNORED_TYPES.getPath());
        for (String name : ignoredTypeNames) {
            Material material = Material.matchMaterial(name);
            if (material == null) {
                plugin.logInfo("Invalid material name in setting {0}.", Setting.DESIGN_SPACE_CHECK_IGNORED_TYPES);
                continue;
            }
            spaceCheckIgnoredTypes.add(material);
        }

        slotMachineNamePattern = config.getString(Setting.SLOT_MACHINE_NAME_PATTERN.getPath(), DEFAULT_SLOT_MACHINE_NAME_PATTERN);
        if (!slotMachineNamePattern.contains("{0}")) {
            plugin.logInfo("Missing id placeholder in setting {0}.", Setting.SLOT_MACHINE_NAME_PATTERN);
            designNamePattern = DEFAULT_SLOT_MACHINE_NAME_PATTERN;
        }
        slotMachineUseLimit = config.getInt(Setting.SLOT_MACHINE_USE_LIMIT.getPath(), 1);

        coinType = Material.matchMaterial(config.getString(Setting.COIN_TYPE.getPath(), DEFAULT_COIN_TYPE.getKey().getKey()));
        if (coinType == null) {
            plugin.logInfo("Invalid material name in setting {0}.", Setting.COIN_TYPE);
            coinType = DEFAULT_COIN_TYPE;
        }
        useCommonCoinItem = config.getBoolean(Setting.COIN_USE_COMMON_ITEM.getPath());
        coinPrice = config.getDouble(Setting.COIN_PRICE.getPath(), DEFAULT_COIN_PRICE);
        if (coinPrice <= 0) {
            plugin.logInfo("The value of setting {0} must be greater than 0.", Setting.COIN_TYPE);
            coinPrice = DEFAULT_COIN_PRICE;
        }
    }

    @Override
    public void unload() {}
    
    @Override
    public void reload() {
        plugin.reloadConfig();
        load();
    }

    public boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getDesignNamePattern() {
        return designNamePattern;
    }

    public String getSlotMachineNamePattern() {
        return slotMachineNamePattern;
    }

    public int getSlotMachineUseLimit() {
        return slotMachineUseLimit;
    }

    public boolean isSpaceCheckEnabled() {
        return spaceCheckEnabled;
    }

    public List<Material> getSpaceCheckIgnoredTypes() {
        return Collections.unmodifiableList(spaceCheckIgnoredTypes);
    }

    public Material getCoinType() {
        return coinType;
    }

    public boolean getUseCommonCoinItem() {
        return useCommonCoinItem;
    }

    public double getCoinPrice() {
        return coinPrice;
    }
}
