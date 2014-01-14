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
	private Configuration c;
	private static boolean DEBUG_MODE_ENABLED;
	private static String LANGUAGE_NAME;
	private static String DEFAULT_SLOT_MACHINE_NAME;
	private static String RAW_SLOT_MACHINE_NAME;
	private static boolean SPACE_CHECK_ENABLED;
	private static Set<Material> SPACE_CHECK_IGNORED_BLOCKS;
	private static ItemStack COIN_ITEM;
	private static boolean COIN_COMMON_ITEM_ENABLED;
	private static double COIN_PRICE;
	private static boolean LIMITED_USAGE_ENABLED;
	private static int LIMITED_USAGE_AMOUNT;
	private static String DEFAULT_DESIGN_NAME;
	private static String RAW_DESIGN_NAME;

	public Settings(ItemSlotMachine plugin) {
		this.plugin = plugin;
		c = plugin.loadConfig();
	}

	public void load() throws InvalidValueException {
		DEBUG_MODE_ENABLED = GENERAL_SETTINGS.getBoolean(c, "Debug_Mode_Enabled");
		LANGUAGE_NAME = GENERAL_SETTINGS.getString(c, "Language_Name");
		if (LANGUAGE_NAME == null)
			throw new InvalidValueException("Language_Name", GENERAL_SETTINGS, "is null");
		DEFAULT_SLOT_MACHINE_NAME = SLOT_MACHINE_SETTINGS.getString(c, "Default_Name");
		if (DEFAULT_SLOT_MACHINE_NAME == null)
			throw new InvalidValueException("Default_Name", SLOT_MACHINE_SETTINGS, "is null");
		else if (DEFAULT_SLOT_MACHINE_NAME.matches(".+<num>.+"))
			throw new InvalidValueException("Default_Name", SLOT_MACHINE_SETTINGS, "has <num> at an invalid position (middle)");
		else if (!DEFAULT_SLOT_MACHINE_NAME.contains("<num>"))
			DEFAULT_SLOT_MACHINE_NAME += "<num>";
		RAW_SLOT_MACHINE_NAME = DEFAULT_SLOT_MACHINE_NAME.replace("<num>", "");
		SPACE_CHECK_ENABLED = SPACE_CHECK.getBoolean(c, "Enabled");
		if (SPACE_CHECK_ENABLED) {
			SPACE_CHECK_IGNORED_BLOCKS = new HashSet<Material>();
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
					SPACE_CHECK_IGNORED_BLOCKS.add(m);
				}
			}
		}
		String coinString = COIN_SETTINGS.getString(c, "Item");
		if (coinString == null)
			throw new InvalidValueException("Item", COIN_SETTINGS, "is null");
		try {
			COIN_ITEM = ItemFactory.fromString(coinString);
		} catch (Exception e) {
			throw new InvalidValueException("Item", COIN_SETTINGS, e.getMessage());
		}
		COIN_COMMON_ITEM_ENABLED = COIN_SETTINGS.getBoolean(c, "Common_Item_Enabled");
		COIN_PRICE = COIN_SETTINGS.getDouble(c, "Price");
		if (COIN_PRICE < 0)
			throw new InvalidValueException("Price", COIN_SETTINGS, "is invalid (lower than 0)");
		LIMITED_USAGE_ENABLED = LIMITED_USAGE.getBoolean(c, "Enabled");
		if (LIMITED_USAGE_ENABLED) {
			LIMITED_USAGE_AMOUNT = LIMITED_USAGE.getInt(c, "Amount");
			if (LIMITED_USAGE_AMOUNT < 1)
				throw new InvalidValueException("Amount", LIMITED_USAGE, "is invalid (lower than 1)");
		}
		DEFAULT_DESIGN_NAME = DESIGN_SETTINGS.getString(c, "Default_Name");
		if (DEFAULT_DESIGN_NAME == null)
			throw new InvalidValueException("Default_Name", DESIGN_SETTINGS, "is null");
		else if (DEFAULT_DESIGN_NAME.matches(".+<num>.+"))
			throw new InvalidValueException("Default_Name", DESIGN_SETTINGS, "has <num> at an invalid position (middle)");
		else if (!DEFAULT_DESIGN_NAME.contains("<num>"))
			DEFAULT_DESIGN_NAME += "<num>";
		RAW_DESIGN_NAME = DEFAULT_DESIGN_NAME.replace("<num>", "");
	}

	public void reload() throws InvalidValueException {
		plugin.reloadConfig();
		c = plugin.getConfig();
		load();
	}

	public static boolean isDebugModeEnabled() {
		return DEBUG_MODE_ENABLED;
	}

	public static String getLanguageName() {
		return LANGUAGE_NAME;
	}

	public static boolean isSpaceCheckEnabled() {
		return SPACE_CHECK_ENABLED;
	}

	public static Set<Material> getSpaceCheckIgnoredBlocks() {
		return Collections.unmodifiableSet(SPACE_CHECK_IGNORED_BLOCKS);
	}

	public static boolean isBlockIgnored(Material m) {
		return SPACE_CHECK_IGNORED_BLOCKS.contains(m);
	}

	public static ItemStack getCoinItem() {
		return COIN_ITEM;
	}

	public static String getDefaultSlotMachineName() {
		return DEFAULT_SLOT_MACHINE_NAME;
	}

	public static String getRawSlotMachineName() {
		return RAW_SLOT_MACHINE_NAME;
	}

	public static boolean isCommonCoinItemEnabled() {
		return COIN_COMMON_ITEM_ENABLED;
	}

	public static double getCoinPrice() {
		return COIN_PRICE;
	}

	public static boolean isLimitedUsageEnabled() {
		return LIMITED_USAGE_ENABLED;
	}

	public static int getLimitedUsageAmount() {
		return LIMITED_USAGE_AMOUNT;
	}

	public static String getDefaultDesignName() {
		return DEFAULT_DESIGN_NAME;
	}

	public static String getRawDesignName() {
		return RAW_DESIGN_NAME;
	}
}