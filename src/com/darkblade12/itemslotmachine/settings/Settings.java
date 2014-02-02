package com.darkblade12.itemslotmachine.settings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.item.ItemFactory;

@SuppressWarnings("deprecation")
public final class Settings {
	private static final SimpleSection GENERAL_SETTINGS = new SimpleSection("General_Settings");
	private static final SimpleSection SLOT_MACHINE_SETTINGS = new SimpleSection("Slot_Machine_Settings");
	private static final SimpleSection SPACE_CHECK = new SimpleSection(SLOT_MACHINE_SETTINGS, "Space_Check");
	private static final SimpleSection COIN_SETTINGS = new SimpleSection(SLOT_MACHINE_SETTINGS, "Coin_Settings");
	private static final SimpleSection LIMITED_USAGE = new SimpleSection(SLOT_MACHINE_SETTINGS, "Limited_Usage");
	private static final SimpleSection DESIGN_SETTINGS = new SimpleSection("Design_Settings");
	private static final String BLOCK_LIST_FORMAT = "(\\d+|[\\w\\s]+)(, (\\d+|[\\w\\s]+))*";
	private ItemSlotMachine plugin;
	private static boolean debugModeEnabled;
	private static String languageName;
	private static String defaultSlotMachineName;
	private static String rawSlotMachineName;
	private static boolean spaceCheckEnabled;
	private static Set<Material> spaceCheckIgnoredBlocks;
	private static ItemStack cointItem;
	private static boolean coinCommonItemEnabled;
	private static double coinPrice;
	private static boolean limitedUsageEnabled;
	private static int limitedUsageAmount;
	private static String defaultDesignName;
	private static String rawDesignName;

	public Settings(ItemSlotMachine plugin) {
		this.plugin = plugin;
	}

	public void load() throws InvalidValueException {
		Configuration c = plugin.loadConfig();
		debugModeEnabled = GENERAL_SETTINGS.getBoolean(c, "Debug_Mode_Enabled");
		languageName = GENERAL_SETTINGS.getString(c, "Language_Name");
		if (languageName == null)
			throw new InvalidValueException("Language_Name", GENERAL_SETTINGS, "is null");
		defaultSlotMachineName = SLOT_MACHINE_SETTINGS.getString(c, "Default_Name");
		if (defaultSlotMachineName == null)
			throw new InvalidValueException("Default_Name", SLOT_MACHINE_SETTINGS, "is null");
		else if (defaultSlotMachineName.matches(".+<num>.+"))
			throw new InvalidValueException("Default_Name", SLOT_MACHINE_SETTINGS, "has <num> at an invalid position (middle)");
		else if (!defaultSlotMachineName.contains("<num>"))
			defaultSlotMachineName += "<num>";
		rawSlotMachineName = defaultSlotMachineName.replace("<num>", "");
		spaceCheckEnabled = SPACE_CHECK.getBoolean(c, "Enabled");
		if (spaceCheckEnabled) {
			spaceCheckIgnoredBlocks = new HashSet<Material>();
			String ignoredBlocksString = SPACE_CHECK.getString(c, "Ignored_Blocks");
			if (ignoredBlocksString != null) {
				if (!ignoredBlocksString.matches(BLOCK_LIST_FORMAT))
					throw new InvalidValueException("Ignored_Blocks", SPACE_CHECK, "has an invalid format");
				for (String b : ignoredBlocksString.split(", ")) {
					Material m;
					boolean id = true;
					try {
						m = Material.getMaterial(Integer.parseInt(b));
					} catch (Exception e) {
						id = false;
						m = Material.matchMaterial(b);
					}
					if (m == null || !m.isBlock())
						throw new InvalidValueException("Ignored_Blocks", SPACE_CHECK, "contains an invalid block " + (id ? "id" : "name"));
					spaceCheckIgnoredBlocks.add(m);
				}
			}
		}
		String coinString = COIN_SETTINGS.getString(c, "Item");
		if (coinString == null)
			throw new InvalidValueException("Item", COIN_SETTINGS, "is null");
		try {
			cointItem = ItemFactory.fromString(coinString);
		} catch (Exception e) {
			throw new InvalidValueException("Item", COIN_SETTINGS, e.getMessage());
		}
		coinCommonItemEnabled = COIN_SETTINGS.getBoolean(c, "Common_Item_Enabled");
		coinPrice = COIN_SETTINGS.getDouble(c, "Price");
		if (coinPrice < 0)
			throw new InvalidValueException("Price", COIN_SETTINGS, "is invalid (lower than 0)");
		limitedUsageEnabled = LIMITED_USAGE.getBoolean(c, "Enabled");
		if (limitedUsageEnabled) {
			limitedUsageAmount = LIMITED_USAGE.getInt(c, "Amount");
			if (limitedUsageAmount < 1)
				throw new InvalidValueException("Amount", LIMITED_USAGE, "is invalid (lower than 1)");
		}
		defaultDesignName = DESIGN_SETTINGS.getString(c, "Default_Name");
		if (defaultDesignName == null)
			throw new InvalidValueException("Default_Name", DESIGN_SETTINGS, "is null");
		else if (defaultDesignName.matches(".+<num>.+"))
			throw new InvalidValueException("Default_Name", DESIGN_SETTINGS, "has <num> at an invalid position (middle)");
		else if (!defaultDesignName.contains("<num>"))
			defaultDesignName += "<num>";
		rawDesignName = defaultDesignName.replace("<num>", "");
	}

	public void reload() throws InvalidValueException {
		plugin.reloadConfig();
		load();
	}

	public static boolean isDebugModeEnabled() {
		return debugModeEnabled;
	}

	public static String getLanguageName() {
		return languageName;
	}

	public static boolean isSpaceCheckEnabled() {
		return spaceCheckEnabled;
	}

	public static Set<Material> getSpaceCheckIgnoredBlocks() {
		return Collections.unmodifiableSet(spaceCheckIgnoredBlocks);
	}

	public static boolean isBlockIgnored(Material m) {
		return spaceCheckIgnoredBlocks.contains(m);
	}

	public static ItemStack getCoinItem() {
		return cointItem;
	}

	public static String getDefaultSlotMachineName() {
		return defaultSlotMachineName;
	}

	public static String getRawSlotMachineName() {
		return rawSlotMachineName;
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

	public static String getDefaultDesignName() {
		return defaultDesignName;
	}

	public static String getRawDesignName() {
		return rawDesignName;
	}
}