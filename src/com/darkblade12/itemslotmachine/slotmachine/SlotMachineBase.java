package com.darkblade12.itemslotmachine.slotmachine;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.cuboid.Cuboid;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.hook.VaultHook;
import com.darkblade12.itemslotmachine.item.ItemList;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reader.CompressedStringReader;
import com.darkblade12.itemslotmachine.reader.ConfigReader;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.safe.SafeLocation;
import com.darkblade12.itemslotmachine.settings.InvalidValueException;
import com.darkblade12.itemslotmachine.settings.SimpleSection;
import com.darkblade12.itemslotmachine.sign.SignUpdater;
import com.darkblade12.itemslotmachine.slotmachine.combo.Combo;
import com.darkblade12.itemslotmachine.slotmachine.combo.ComboList;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.ItemPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.MoneyPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.command.CommandList;
import com.darkblade12.itemslotmachine.slotmachine.command.Placeholder;
import com.darkblade12.itemslotmachine.slotmachine.sound.SoundList;
import com.darkblade12.itemslotmachine.statistic.types.SlotMachineStatistic;

public abstract class SlotMachineBase implements Nameable {
	private static final SimpleSection GENERAL_SETTINGS = new SimpleSection("General_Settings");
	private static final SimpleSection INDIVIDUAL_PERMISSION = new SimpleSection(GENERAL_SETTINGS, "Individual_Permission");
	private static final SimpleSection HALT_SETTINGS = new SimpleSection(GENERAL_SETTINGS, "Halt_Settings");
	private static final SimpleSection AUTOMATIC_HALT = new SimpleSection(HALT_SETTINGS, "Automatic_Halt");
	private static final SimpleSection PREDETERMINED_WINNING_CHANCE = new SimpleSection(GENERAL_SETTINGS, "Predetermined_Winning_Chance");
	private static final SimpleSection SOUND_SETTINGS = new SimpleSection(GENERAL_SETTINGS, "Sound_Settings");
	private static final SimpleSection TICKING_SOUNDS = new SimpleSection(SOUND_SETTINGS, "Ticking_Sounds");
	private static final SimpleSection WIN_SOUNDS = new SimpleSection(SOUND_SETTINGS, "Win_Sounds");
	private static final SimpleSection LOSE_SOUNDS = new SimpleSection(SOUND_SETTINGS, "Lose_Sounds");
	private static final SimpleSection PLAYER_LOCK = new SimpleSection(GENERAL_SETTINGS, "Player_Lock");
	private static final SimpleSection COMMAND_EXECUTION = new SimpleSection(GENERAL_SETTINGS, "Command_Execution");
	private static final SimpleSection MONEY_POT_SETTINGS = new SimpleSection("Money_Pot_Settings");
	private static final SimpleSection MONEY_POT_HOUSE_CUT = new SimpleSection(MONEY_POT_SETTINGS, "House_Cut");
	private static final SimpleSection MONEY_POT_COMBO_SETTINGS = new SimpleSection(MONEY_POT_SETTINGS, "Combo_Settings");
	private static final SimpleSection ITEM_POT_SETTINGS = new SimpleSection("Item_Pot_Settings");
	private static final SimpleSection ITEM_POT_HOUSE_CUT = new SimpleSection(ITEM_POT_SETTINGS, "House_Cut");
	private static final SimpleSection ITEM_POT_COMBO_SETTINGS = new SimpleSection(ITEM_POT_SETTINGS, "Combo_Settings");
	private static final Random RANDOM = new Random();
	protected ItemSlotMachine plugin;
	protected String name;
	protected CompressedStringReader instanceReader;
	protected SlotMachineStatistic statistic;
	protected ConfigReader configReader;
	protected Design design;
	protected SafeLocation center;
	protected Direction initialDirection;
	protected SafeLocation sign, slot;
	protected Cuboid region;
	protected int activationAmount;
	protected ItemList itemIcons;
	protected boolean creativeUsageEnabled;
	protected boolean fireworksEnabled;
	protected boolean individualPermissionEnabled;
	protected String individualPermission;
	protected int[] haltTickDelay;
	protected boolean automaticHaltEnabled;
	protected int automaticHaltTicks;
	protected boolean predeterminedWinningChanceEnabled;
	protected int predeterminedWinningChanceMin;
	protected int predeterminedWinningChanceMax;
	protected boolean tickingSoundsEnabled;
	protected boolean tickingSoundsBroadcast;
	protected SoundList tickingSounds;
	protected boolean winSoundsEnabled;
	protected boolean winSoundsBroadcast;
	protected SoundList winSounds;
	protected boolean loseSoundsEnabled;
	protected boolean loseSoundsBroadcast;
	protected SoundList loseSounds;
	protected boolean playerLockEnabled;
	protected int playerLockTime;
	protected boolean commandExecutionEnabled;
	protected CommandList commands;
	protected boolean moneyPotEnabled;
	protected double moneyPotDefaultSize;
	protected double moneyPotRaise;
	protected boolean moneyPotHouseCutEnabled;
	protected boolean moneyPotHouseCutPercentage;
	protected double moneyPotHouseCutAmount;
	protected boolean moneyPotCombosEnabled;
	protected ComboList<MoneyPotCombo> moneyPotCombos;
	protected boolean itemPotEnabled;
	protected ItemList itemPotDefaultItems;
	protected ItemList itemPotRaise;
	protected boolean itemPotHouseCutEnabled;
	protected int itemPotHouseCutAmount;
	protected boolean itemPotCombosEnabled;
	protected ComboList<ItemPotCombo> itemPotCombos;
	protected double moneyPot;
	protected ItemList itemPot;

	public SlotMachineBase(ItemSlotMachine plugin, String name) throws Exception {
		this.plugin = plugin;
		this.name = name;
		instanceReader = new CompressedStringReader(name + ".instance", "plugins/ItemSlotMachine/slot machines/");
		String s;
		try {
			s = instanceReader.readFromFile();
		} catch (Exception e) {
			throw new Exception("Failed to read " + instanceReader.getOuputFileName());
		}
		String[] p = s.split("#");
		design = plugin.designManager.getDesign(p[0]);
		if (design == null)
			throw new Exception("The design of this slot machine does no longer exist");
		center = SafeLocation.fromString(p[1]);
		initialDirection = Direction.valueOf(p[2]);
		Location l = center.getBukkitLocation();
		sign = design.getSign().getSafeLocation(l, initialDirection);
		slot = design.getSlot().getSafeLocation(l, initialDirection);
		region = design.getRegion().getCuboid(l, initialDirection);
		statistic = SlotMachineStatistic.fromFile(name);
		configReader = new ConfigReader(plugin, plugin.template, name + ".yml", "plugins/ItemSlotMachine/slot machines/");
		if (!configReader.readConfig())
			throw new Exception("Failed to read " + configReader.getOuputFileName());
		loadSettings();
		if (p.length == 3) {
			moneyPot = moneyPotDefaultSize;
			if (itemPotEnabled) {
				itemPot = itemPotDefaultItems.clone();
			}
			saveInstance();
		} else {
			moneyPot = Double.parseDouble(p[3]);
			itemPot = p.length == 5 ? ItemList.fromString(p[4]) : new ItemList();
		}
		updateSign();
	}

	private void loadSettings() throws InvalidValueException {
		activationAmount = GENERAL_SETTINGS.getInt(configReader.config, "Activation_Amount");
		if (activationAmount < 0)
			throw new InvalidValueException("Activation_Amount", GENERAL_SETTINGS, "is invalid (lower than 0)");
		String itemIconsString = GENERAL_SETTINGS.getString(configReader.config, "Item_Icons");
		if (itemIconsString == null)
			throw new InvalidValueException("Item_Icons", GENERAL_SETTINGS, "is null (empty)");
		try {
			itemIcons = ItemList.fromString(itemIconsString, false);
		} catch (Exception e) {
			throw new InvalidValueException("Item_Icons", GENERAL_SETTINGS, e.getMessage());
		}
		creativeUsageEnabled = GENERAL_SETTINGS.getBoolean(configReader.config, "Creative_Usage_Enabled");
		fireworksEnabled = GENERAL_SETTINGS.getBoolean(configReader.config, "Fireworks_Enabled");
		individualPermissionEnabled = INDIVIDUAL_PERMISSION.getBoolean(configReader.config, "Enabled");
		if (individualPermissionEnabled) {
			individualPermission = INDIVIDUAL_PERMISSION.getString(configReader.config, "Permission");
			if (individualPermission == null)
				throw new InvalidValueException("Permission", INDIVIDUAL_PERMISSION, "is null");
			individualPermission = individualPermission.replace("<name>", name);
		}
		haltTickDelay = new int[3];
		String haltTickDelayString = HALT_SETTINGS.getString(configReader.config, "Tick_Delay");
		if (haltTickDelayString == null)
			throw new InvalidValueException("Tick_Delay", HALT_SETTINGS, "is null");
		int index = 0;
		for (String tickDelay : haltTickDelayString.split("-")) {
			if (index == 3)
				break;
			try {
				haltTickDelay[index] = Integer.parseInt(tickDelay);
			} catch (Exception e) {
				throw new InvalidValueException("Tick_Delay", HALT_SETTINGS, "contains invalid number");
			}
			index++;
		}
		automaticHaltEnabled = AUTOMATIC_HALT.getBoolean(configReader.config, "Enabled");
		if (automaticHaltEnabled) {
			automaticHaltTicks = AUTOMATIC_HALT.getInt(configReader.config, "Ticks");
			if (automaticHaltTicks < 1)
				throw new InvalidValueException("Ticks", AUTOMATIC_HALT, "is invalid (lower than 1)");
		}
		predeterminedWinningChanceEnabled = PREDETERMINED_WINNING_CHANCE.getBoolean(configReader.config, "Enabled");
		if (predeterminedWinningChanceEnabled) {
			String valueString = PREDETERMINED_WINNING_CHANCE.getString(configReader.config, "Value");
			if (valueString == null)
				throw new InvalidValueException("Value", PREDETERMINED_WINNING_CHANCE, "is null");
			try {
				String[] v = valueString.split("/");
				predeterminedWinningChanceMin = Integer.parseInt(v[0]);
				predeterminedWinningChanceMax = Integer.parseInt(v[1]);
				if (predeterminedWinningChanceMin > predeterminedWinningChanceMax)
					throw new InvalidValueException("Value", PREDETERMINED_WINNING_CHANCE, "is invalid (min chance greater than max chance)");
				else if (predeterminedWinningChanceMin < 0)
					throw new InvalidValueException("Value", PREDETERMINED_WINNING_CHANCE, "is invalid (min chance lower than 0)");
				else if (predeterminedWinningChanceMax < 0)
					throw new InvalidValueException("Value", PREDETERMINED_WINNING_CHANCE, "is invalid (max chance lower than 0)");
			} catch (Exception e) {
				throw new InvalidValueException("Value", PREDETERMINED_WINNING_CHANCE, "has an invalid format");
			}
		}
		tickingSoundsEnabled = TICKING_SOUNDS.getBoolean(configReader.config, "Enabled");
		if (tickingSoundsEnabled) {
			tickingSoundsBroadcast = TICKING_SOUNDS.getBoolean(configReader.config, "Broadcast");
			String tickingSoundsString = TICKING_SOUNDS.getString(configReader.config, "Sounds");
			if (tickingSoundsString == null)
				throw new InvalidValueException("Sounds", TICKING_SOUNDS, "is null (empty)");
			try {
				tickingSounds = SoundList.fromString(tickingSoundsString);
			} catch (Exception e) {
				throw new InvalidValueException("Sounds", TICKING_SOUNDS, e.getMessage());
			}
		}
		winSoundsEnabled = WIN_SOUNDS.getBoolean(configReader.config, "Enabled");
		if (winSoundsEnabled) {
			winSoundsBroadcast = WIN_SOUNDS.getBoolean(configReader.config, "Broadcast");
			String winSoundsString = WIN_SOUNDS.getString(configReader.config, "Sounds");
			if (winSoundsString == null)
				throw new InvalidValueException("Sounds", WIN_SOUNDS, "is null (empty)");
			try {
				winSounds = SoundList.fromString(winSoundsString);
			} catch (Exception e) {
				throw new InvalidValueException("Sounds", WIN_SOUNDS, e.getMessage());
			}
		}
		loseSoundsEnabled = LOSE_SOUNDS.getBoolean(configReader.config, "Enabled");
		if (loseSoundsEnabled) {
			loseSoundsBroadcast = LOSE_SOUNDS.getBoolean(configReader.config, "Broadcast");
			String loseSoundsString = LOSE_SOUNDS.getString(configReader.config, "Sounds");
			if (loseSoundsString == null)
				throw new InvalidValueException("Sounds", LOSE_SOUNDS, "is null (empty)");
			try {
				loseSounds = SoundList.fromString(loseSoundsString);
			} catch (Exception e) {
				throw new InvalidValueException("Sounds", LOSE_SOUNDS, e.getMessage());
			}
		}
		playerLockEnabled = PLAYER_LOCK.getBoolean(configReader.config, "Enabled");
		if (playerLockEnabled) {
			playerLockTime = PLAYER_LOCK.getInt(configReader.config, "Time");
			if (playerLockTime < 1)
				throw new InvalidValueException("Time", PLAYER_LOCK, "is invalid (lower than 1)");
		}
		commandExecutionEnabled = COMMAND_EXECUTION.getBoolean(configReader.config, "Enabled");
		if (commandExecutionEnabled) {
			String commandsString = COMMAND_EXECUTION.getString(configReader.config, "Commands");
			if (commandsString == null)
				throw new InvalidValueException("Commands", COMMAND_EXECUTION, "is null (empty)");
			try {
				commands = CommandList.fromString(commandsString);
			} catch (Exception e) {
				throw new InvalidValueException("Commands", COMMAND_EXECUTION, e.getMessage());
			}
		}
		moneyPotEnabled = MONEY_POT_SETTINGS.getBoolean(configReader.config, "Enabled");
		if (moneyPotEnabled) {
			moneyPotDefaultSize = MONEY_POT_SETTINGS.getDouble(configReader.config, "Default_Size");
			if (moneyPotDefaultSize < 0)
				throw new InvalidValueException("Default_Size", MONEY_POT_SETTINGS, "is invalid (lower than 0)");
			moneyPotRaise = MONEY_POT_SETTINGS.getDouble(configReader.config, "Pot_Raise");
			if (moneyPotRaise < 0)
				throw new InvalidValueException("Pot_Raise", MONEY_POT_SETTINGS, "is invalid (lower than 0)");
			moneyPotHouseCutEnabled = MONEY_POT_HOUSE_CUT.getBoolean(configReader.config, "Enabled");
			if (moneyPotHouseCutEnabled) {
				moneyPotHouseCutPercentage = MONEY_POT_HOUSE_CUT.getBoolean(configReader.config, "Percentage");
				moneyPotHouseCutAmount = MONEY_POT_HOUSE_CUT.getDouble(configReader.config, "Amount");
				if (moneyPotHouseCutAmount < 1)
					throw new InvalidValueException("Amount", MONEY_POT_HOUSE_CUT, "is invalid (lower than 1)");
			}
		}
		moneyPotCombosEnabled = MONEY_POT_COMBO_SETTINGS.getBoolean(configReader.config, "Enabled");
		if (moneyPotCombosEnabled) {
			String moneyPotCombosString = MONEY_POT_COMBO_SETTINGS.getString(configReader.config, "Combos");
			if (moneyPotCombosString == null)
				throw new InvalidValueException("Combos", MONEY_POT_COMBO_SETTINGS, "is null (empty)");
			try {
				moneyPotCombos = ComboList.fromString1(moneyPotCombosString);
			} catch (Exception e) {
				throw new InvalidValueException("Combos", MONEY_POT_COMBO_SETTINGS, e.getMessage());
			}
		}
		itemPotEnabled = ITEM_POT_SETTINGS.getBoolean(configReader.config, "Enabled");
		if (itemPotEnabled) {
			String defaultItemsString = ITEM_POT_SETTINGS.getString(configReader.config, "Default_Items");
			try {
				itemPotDefaultItems = defaultItemsString == null ? new ItemList() : ItemList.fromString(defaultItemsString);
			} catch (Exception e) {
				throw new InvalidValueException("Default_Items", ITEM_POT_SETTINGS, e.getMessage());
			}
			String potRaiseString = ITEM_POT_SETTINGS.getString(configReader.config, "Pot_Raise");
			try {
				itemPotRaise = potRaiseString == null ? new ItemList() : ItemList.fromString(potRaiseString);
			} catch (Exception e) {
				throw new InvalidValueException("Pot_Raise", ITEM_POT_SETTINGS, e.getMessage());
			}
			itemPotHouseCutEnabled = ITEM_POT_HOUSE_CUT.getBoolean(configReader.config, "Enabled");
			if (itemPotHouseCutEnabled) {
				itemPotHouseCutAmount = ITEM_POT_HOUSE_CUT.getInt(configReader.config, "Amount");
				if (itemPotHouseCutAmount < 1)
					throw new InvalidValueException("Amount", ITEM_POT_HOUSE_CUT, "is invalid (lower than 1)");
			}
		}
		itemPotCombosEnabled = ITEM_POT_COMBO_SETTINGS.getBoolean(configReader.config, "Enabled");
		if (itemPotCombosEnabled) {
			String itemPotCombosString = ITEM_POT_COMBO_SETTINGS.getString(configReader.config, "Combos");
			if (itemPotCombosString == null)
				throw new InvalidValueException("Combos", ITEM_POT_COMBO_SETTINGS, "is null (empty)");
			try {
				itemPotCombos = ComboList.fromString2(itemPotCombosString);
			} catch (Exception e) {
				throw new InvalidValueException("Combos", ITEM_POT_COMBO_SETTINGS, e.getMessage());
			}
		}
		if (!moneyPotEnabled && !itemPotEnabled)
			throw new IllegalArgumentException("Money and item pot are disabled");
	}

	public void saveInstance() {
		instanceReader.saveToFile(design.getName() + "#" + center + "#" + initialDirection.name() + "#" + moneyPot + (itemPotEnabled && itemPot.size() > 0 ? "#" + itemPot : ""));
	}

	public void updateSign() {
		Sign s = getSignInstance();
		if (s != null) {
			String[] lines = null;
			if (moneyPotEnabled && itemPotEnabled)
				lines = new String[] { plugin.messageManager.sign_pot_money(moneyPot), plugin.messageManager.sign_pot_spacer(), plugin.messageManager.sign_pot_items(itemPot.size()), plugin.messageManager.sign_pot_spacer() };
			else if (moneyPotEnabled)
				lines = new String[] { plugin.messageManager.sign_pot_money(moneyPot), plugin.messageManager.sign_pot_spacer(), plugin.messageManager.sign_pot_spacer(), plugin.messageManager.sign_pot_spacer() };
			else if (itemPotEnabled)
				lines = new String[] { plugin.messageManager.sign_pot_items(itemPot.size()), plugin.messageManager.sign_pot_spacer(), plugin.messageManager.sign_pot_spacer(), plugin.messageManager.sign_pot_spacer() };
			lines = SignUpdater.validateLines(lines, 0, 2);
			for (int i = 0; i < lines.length; i++)
				s.setLine(i, lines[i]);
			s.update(true);
		}
	}

	public void update() {
		saveInstance();
		updateSign();
	}

	protected void insertCoins(Player p) {
		if (p.getGameMode() != GameMode.CREATIVE) {
			int a = activationAmount;
			ItemStack[] c = p.getInventory().getContents();
			for (int i = 0; i < c.length; i++) {
				ItemStack s = c[i];
				if (s != null && plugin.coinManager.isCoin(s)) {
					int amount = s.getAmount();
					if (amount > a) {
						s.setAmount(amount - a);
						break;
					} else if (amount == a) {
						s.setType(Material.AIR);
						break;
					} else if (a > amount) {
						s.setType(Material.AIR);
						a -= amount;
						if (a == 0)
							break;
					}
					c[i] = s;
				}
			}
			p.getInventory().setContents(c);
			p.updateInventory();
		}
	}

	public void setMoneyPot(double moneyPot) {
		this.moneyPot = moneyPot < 0 ? 0 : moneyPot;
		update();
	}

	public void setItemPot(ItemList itemPot) {
		this.itemPot = itemPot.clone();
		update();
	}

	public double depositPotMoney(double amount) {
		setMoneyPot(moneyPot + amount);
		return moneyPot;
	}

	public double withdrawPotMoney(double amount) {
		setMoneyPot(moneyPot - amount);
		return moneyPot;
	}

	public ItemList depositPotItems(ItemList items) {
		itemPot.addAll(items.clone());
		update();
		return itemPot.clone();
	}

	protected void raisePot() {
		if (moneyPotEnabled)
			depositPotMoney(moneyPotRaise);
		if (itemPotEnabled)
			depositPotItems(itemPotRaise);
	}

	public double resetMoneyPot() {
		setMoneyPot(moneyPotDefaultSize);
		return moneyPot;
	}

	public void clearMoneyPot() {
		setMoneyPot(0);
	}

	public ItemList resetItemPot() {
		setItemPot(itemPotDefaultItems);
		return itemPot.clone();
	}

	public void clearItemPot() {
		itemPot.clear();
		update();
	}

	private boolean isRegularWin(ItemStack[] icons) {
		return icons[0].isSimilar(icons[1]) && icons[1].isSimilar(icons[2]);
	}

	private boolean isMoneyPotComboWin(ItemStack[] icons) {
		if (!getMoneyPotCombosEnabled()) {
			return false;
		}
		for (MoneyPotCombo combo : moneyPotCombos) {
			if (combo.isActivated(icons)) {
				return true;
			}
		}
		return false;
	}

	private boolean isItemPotComboWin(ItemStack[] icons) {
		if (!getItemPotCombosEnabled()) {
			return false;
		}
		for (ItemPotCombo combo : itemPotCombos) {
			if (combo.isActivated(icons)) {
				return true;
			}
		}
		return false;
	}

	private boolean isComboWin(ItemStack[] icons) {
		return isMoneyPotComboWin(icons) || isItemPotComboWin(icons);
	}

	private boolean isWin(ItemStack[] icons) {
		return isRegularWin(icons) || isComboWin(icons);
	}

	private ItemStack[] generateWinIcons() {
		ComboList<? extends Combo> combos = null;
		if (getMoneyPotCombosEnabled() && (!getItemPotCombosEnabled() || RANDOM.nextBoolean())) {
			combos = moneyPotCombos;
		} else if (getItemPotCombosEnabled()) {
			combos = itemPotCombos;
		}
		boolean regular = combos == null ? true : RANDOM.nextInt(100) < 80;
		ItemStack[] icons = new ItemStack[3];
		if (regular) {
			ItemStack icon = getRandomIcon();
			icons[0] = icon;
			icons[1] = icon;
			icons[2] = icon;
		} else {
			Combo combo = combos.get(RANDOM.nextInt(combos.size()));
			ItemStack[] comboIcons = combo.getIcons();
			for (int i = 0; i < 3; i++) {
				ItemStack finalIcon = comboIcons[i];
				if (finalIcon.getType() == Material.AIR) {
					finalIcon = getRandomIcon();
				}
				icons[i] = finalIcon;
			}
		}
		return icons;
	}

	private ItemStack[] generateLoseIcons(int loops) {
		if (loops == 4) {
			/* Couldn't find a lose combination! */
			return null;
		}
		ItemStack[] icons = getRandomIcons();
		if (!isWin(icons)) {
			return icons;
		}
		int[] positions = new int[3];
		int size = itemIcons.size();
		if (loops < 3) {
			for (int i = 0; i < size; i++) {
				ItemStack icon = itemIcons.get(i);
				for (int j = 0; j < 3; j++) {
					if (!icon.isSimilar(icons[j])) {
						continue;
					}
					positions[j] = i;
				}
			}
		}
		for (int k = positions[0]; k < size; k++) {
			for (int l = positions[1]; l < size; l++) {
				for (int m = positions[2]; m < size; m++) {
					ItemStack[] newIcons = new ItemStack[] { itemIcons.get(k), itemIcons.get(l), itemIcons.get(m) };
					if (isWin(newIcons)) {
						continue;
					}
					return newIcons;
				}
			}
		}
		return generateLoseIcons(loops + 1);

	}

	protected ItemStack[] generateIcons() {
		if (!predeterminedWinningChanceEnabled) {
			return getRandomIcons();
		}
		if (RANDOM.nextInt(predeterminedWinningChanceMax) < predeterminedWinningChanceMin) {
			return generateWinIcons();
		}
		return generateLoseIcons(0);
	}

	protected void executeCommands(String userName, double money, ItemList items) {
		if (commandExecutionEnabled)
			commands.execute(new Placeholder("<user_name>", userName), new Placeholder("<money>", Double.toString(money)), new Placeholder("<currency_name>", money == 1 ? VaultHook.ECONOMY.currencyNameSingular() : VaultHook.ECONOMY.currencyNamePlural()), new Placeholder(
					"<item_amount>", Integer.toString(items.size())), new Placeholder("<items>", plugin.messageManager.itemsToString(items, "")), new Placeholder("<slot_machine>", name));
	}

	public void teleport(Player p) throws IllegalStateException {
		boolean flying = p.isFlying();
		Location l = center.getBukkitLocation();
		for (int i = 1; i <= 5; i++) {
			Block b = design.getSlot().clone().add(0, -i, 0).getBukkitBlock(l, initialDirection);
			if (!b.getType().isSolid() && !b.getRelative(BlockFace.UP).getType().isSolid()) {
				if (b.getRelative(BlockFace.DOWN).getType().isSolid() || flying) {
					Location t = b.getLocation().add(0.5, 0, 0.5);
					t.setYaw(initialDirection.getRotation() * 90);
					p.teleport(t);
					return;
				}
			}
		}
		throw new IllegalStateException("No suitable teleport location found");
	}

	public void rebuild() {
		Location l = center.getBukkitLocation();
		design.destruct(l, initialDirection);
		try {
			design.build(l, initialDirection);
		} catch (Exception e) {
			/* do nothing */
		}
		updateSign();
	}

	public void move(BlockFace b, int amount) throws Exception {
		Location l = center.getBukkitLocation();
		Location n = l.clone().add(b.getModX() * amount, b.getModY() * amount, b.getModZ() * amount);
		design.destruct(l, initialDirection);
		try {
			design.build(n, initialDirection);
			center = SafeLocation.fromBukkitLocation(n);
			sign = design.getSign().getSafeLocation(n, initialDirection);
			slot = design.getSlot().getSafeLocation(n, initialDirection);
			region = design.getRegion().getCuboid(n, initialDirection);
			update();
		} catch (Exception e) {
			design.build(l, initialDirection);
			updateSign();
			throw e;
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	public SlotMachineStatistic getStatistic() {
		return this.statistic;
	}

	public Design getDesign() {
		return this.design;
	}

	public SafeLocation getCenter() {
		return center.clone();
	}

	public Direction getInitialDirection() {
		return this.initialDirection;
	}

	public ItemFrame[] getItemFrameInstances() {
		ItemFrame[] frames = new ItemFrame[3];
		Location l = center.getBukkitLocation();
		for (int i = 0; i < 3; i++) {
			ItemFrame f = design.getItemFrames()[i].getBukkitItemFrame(l, initialDirection);
			if (f == null)
				return null;
			else
				frames[i] = f;
		}
		return frames;
	}

	public SafeLocation getSign() {
		return sign.clone();
	}

	public Sign getSignInstance() {
		try {
			return (Sign) sign.getBukkitBlock().getState();
		} catch (Exception e) {
			return null;
		}
	}

	public SafeLocation getSlot() {
		return slot.clone();
	}

	public boolean hasInteracted(Location l) {
		return slot.noDistance(l);
	}

	public Cuboid getRegion() {
		return this.region;
	}

	public boolean isInsideRegion(Location l) {
		return region.isInside(l);
	}

	public int getActivationAmount() {
		return this.activationAmount;
	}

	public boolean hasEnoughCoins(Player p) {
		if (p.getGameMode() == GameMode.CREATIVE)
			return true;
		int a = activationAmount;
		for (ItemStack i : p.getInventory().getContents())
			if (i != null && plugin.coinManager.isCoin(i))
				if (a == 0)
					return true;
				else if (i.getAmount() >= a)
					return true;
				else
					a -= i.getAmount();
		return false;
	}

	public ItemList getItemIcons() {
		return itemIcons.clone();
	}

	protected ItemStack getRandomIcon() {
		return itemIcons.get(RANDOM.nextInt(itemIcons.size()));
	}

	private ItemStack[] getRandomIcons() {
		return new ItemStack[] { getRandomIcon(), getRandomIcon(), getRandomIcon() };
	}

	public boolean isCreativeUsageEnabled() {
		return this.creativeUsageEnabled;
	}

	public boolean hasFireworksEnabled() {
		return this.fireworksEnabled;
	}

	public boolean isIndividualPermissionEnabled() {
		return this.individualPermissionEnabled;
	}

	public String getIndividualPermission() {
		return this.individualPermission;
	}

	public int[] getHaltTickDelay() {
		return haltTickDelay.clone();
	}

	public boolean isAutomaticHaltEnabled() {
		return this.automaticHaltEnabled;
	}

	public int getAutomaticHaltTicks() {
		return this.automaticHaltTicks;
	}

	public boolean isPredeterminedWinningChanceEnabled() {
		return this.predeterminedWinningChanceEnabled;
	}

	public boolean isPlayerLockEnabled() {
		return this.playerLockEnabled;
	}

	public int getPlayerLockTime() {
		return this.playerLockTime;
	}

	public boolean isCommandExecutionEnabled() {
		return this.commandExecutionEnabled;
	}

	public CommandList getCommands() {
		return commands.clone();
	}

	public boolean isMoneyPotEnabled() {
		return this.moneyPotEnabled;
	}

	public double getMoneyPotDefaultSize() {
		return this.moneyPotDefaultSize;
	}

	public double getMoneyPotRaise() {
		return this.moneyPotRaise;
	}

	public boolean isItemPotEnabled() {
		return this.itemPotEnabled;
	}

	public ItemList getItemPotDefaultItems() {
		return itemPotDefaultItems.clone();
	}

	public ItemList getItemPotRaise() {
		return itemPotRaise.clone();
	}

	public double getMoneyPot() {
		return this.moneyPot;
	}

	public boolean isMoneyPotEmpty() {
		return moneyPot == 0;
	}

	protected boolean getMoneyPotCombosEnabled() {
		return moneyPotCombosEnabled && moneyPotEnabled && VaultHook.isEnabled();
	}

	public ItemList getItemPot() {
		return itemPot.clone();
	}

	public boolean isItemPotEmpty() {
		return itemPot.size() == 0;
	}

	protected boolean getItemPotCombosEnabled() {
		return itemPotCombosEnabled && itemPotEnabled;
	}

	public boolean isPermittedToModify(Player p) {
		return p.hasPermission("ItemSlotMachine.slot.modify." + name) || p.hasPermission("ItemSlotMachine.slot.modify.*") || p.hasPermission("ItemSlotMachine.slot.*") || p.hasPermission("ItemSlotMachine.*");
	}

	public boolean isPermittedToUse(Player p) {
		String permission = individualPermissionEnabled ? individualPermission : "ItemSlotMachine.slot.use";
		return p.hasPermission(permission) || p.hasPermission("ItemSlotMachine.slot.use.*") || p.hasPermission("ItemSlotMachine.slot.*") || p.hasPermission("ItemSlotMachine.*");
	}
}
