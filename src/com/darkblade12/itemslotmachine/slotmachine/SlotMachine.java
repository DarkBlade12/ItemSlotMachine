package com.darkblade12.itemslotmachine.slotmachine;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.hook.VaultHook;
import com.darkblade12.itemslotmachine.item.ItemList;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reader.CompressedStringReader;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.rocket.Rocket;
import com.darkblade12.itemslotmachine.safe.SafeLocation;
import com.darkblade12.itemslotmachine.slotmachine.combo.Action;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.ItemPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.MoneyPotCombo;
import com.darkblade12.itemslotmachine.statistic.Type;
import com.darkblade12.itemslotmachine.statistic.types.PlayerStatistic;

public final class SlotMachine extends SlotMachineBase implements Nameable {
	private boolean broken;
	private String userName;
	private long lockEnd;
	private BukkitTask task;
	private boolean active;
	private boolean halted;

	private SlotMachine(ItemSlotMachine plugin, String name) throws Exception {
		super(plugin, name);
	}

	public static SlotMachine create(ItemSlotMachine plugin, String name, Design d, Player p) throws Exception {
		d.build(p);
		new CompressedStringReader(name + ".instance", "plugins/ItemSlotMachine/slot machines/").saveToFile(d.getName() + "#" + SafeLocation.fromBukkitLocation(p.getLocation()) + "#" + Direction.get(p).name());
		return load(plugin, name);
	}

	public static SlotMachine load(ItemSlotMachine plugin, String name) throws Exception {
		return new SlotMachine(plugin, name);
	}

	private void playTickingSounds() {
		if (tickingSoundsEnabled) {
			Location l = slot.getBukkitLocation();
			if (tickingSoundsBroadcast)
				tickingSounds.play(l);
			else
				tickingSounds.play(getUser(), l);
		}
	}

	private void playWinSounds() {
		if (winSoundsEnabled) {
			Location l = slot.getBukkitLocation();
			if (winSoundsBroadcast)
				winSounds.play(l);
			else
				winSounds.play(getUser(), l);
		}
	}

	private void playLoseSounds() {
		if (loseSoundsEnabled) {
			Location l = slot.getBukkitLocation();
			if (loseSoundsBroadcast)
				loseSounds.play(l);
			else
				loseSounds.play(getUser(), l);
		}
	}

	private void playWinEffect() {
		playWinSounds();
		if (fireworksEnabled)
			Rocket.randomize().displayEffects(plugin, slot.getBukkitLocation().add(0.5, 2, 0.5));
	}

	public void activate(final Player p) {
		final ItemFrame[] frames = getItemFrameInstances();
		broken = !(getSign() != null && frames != null);
		if (broken) {
			p.sendMessage(plugin.messageManager.slot_machine_broken());
			return;
		}
		final ItemStack[] icons = generateIcons();
		if(icons == null) {
			p.sendMessage(plugin.messageManager.slot_machine_broken());
			return;
		}
		insertCoins(p);
		PlayerStatistic s = plugin.statisticManager.getStatistic(p, true);
		s.getObject(Type.TOTAL_SPINS).increaseValue(1);
		if (p.getGameMode() != GameMode.CREATIVE)
			s.getObject(Type.COINS_SPENT).increaseValue(activationAmount);
		s.saveToFile();
		statistic.getObject(Type.TOTAL_SPINS).increaseValue(1);
		statistic.saveToFile();
		raisePot();
		userName = p.getName();
		task = new BukkitRunnable() {
			private int[] ticks = new int[3];
			private int[] delayTicks = new int[3];

			@Override
			public void run() {
				playTickingSounds();
				if (automaticHaltEnabled && ticks[0] == automaticHaltTicks)
					halted = true;
				for (int i = 0; i < 3; i++)
					if (halted ? delayTicks[i] != haltTickDelay[i] : true) {
						if (delayTicks[i] == haltTickDelay[i] - 1 || automaticHaltEnabled && !halted && haltTickDelay[i] == 0 && ticks[i] == automaticHaltTicks - 1) {
							frames[i].setItem(icons[i]);
						} else {
							frames[i].setItem(getRandomIcon());
						}
						if (halted)
							delayTicks[i]++;
						else
							ticks[i]++;
					} else if (i == 2)
						distribute(frames[0].getItem(), frames[1].getItem(), frames[2].getItem());
				updateSign();
			}
		}.runTaskTimer(plugin, 5, 5);
		active = true;
	}

	public void halt() {
		halted = true;
	}

	private void deactivate(boolean manual) {
		active = false;
		halted = false;
		if (task != null)
			task.cancel();
		if (!manual && playerLockEnabled) {
			lockEnd = System.currentTimeMillis() + playerLockTime * 1000;
		} else {
			lockEnd = 0;
			userName = null;
		}
	}

	public void deactivate() {
		deactivate(true);
	}

	private double applyHouseCut(double money) {
		return moneyPotHouseCutEnabled ? money - (moneyPotHouseCutPercentage ? money * (moneyPotHouseCutAmount / 100.0D) : moneyPotHouseCutAmount) : money;
	}

	private ItemList applyHouseCut(ItemList items) {
		if (itemPotHouseCutEnabled)
			items.removeRandom(itemPotHouseCutAmount);
		return items;
	}

	@SuppressWarnings("deprecation")
	private void handleWin(double moneyPrize, ItemList itemPrize, boolean executeCommands) {
		playWinEffect();
		statistic.getObject(Type.WON_SPINS).increaseValue(1);
		statistic.saveToFile();
		Player u = getUser();
		PlayerStatistic p = plugin.statisticManager.getStatistic(u, true);
		p.getObject(Type.WON_SPINS).increaseValue(1);
		if (moneyPrize > 0) {
			moneyPrize = applyHouseCut(moneyPrize);
			p.getObject(Type.WON_MONEY).increaseValue(moneyPrize);
			VaultHook.ECONOMY.depositPlayer(userName, moneyPrize);
		}
		if (itemPrize.size() > 0) {
			itemPrize = applyHouseCut(itemPrize);
			p.getObject(Type.WON_ITEMS).increaseValue(itemPrize.size());
			itemPrize.distribute(u);
		}
		p.saveToFile();
		if (executeCommands)
			executeCommands(userName, moneyPrize, itemPrize);
		u.sendMessage(plugin.messageManager.slot_machine_won(moneyPrize, itemPrize));
	}

	private void distribute(ItemStack... display) {
		MoneyPotCombo m = getMoneyPotCombosEnabled() ? moneyPotCombos.getActivated(display) : null;
		ItemPotCombo i = getItemPotCombosEnabled() ? itemPotCombos.getActivated(display) : null;
		if (display[0].isSimilar(display[1]) && display[1].isSimilar(display[2])) {
			double moneyPrize = 0;
			if (moneyPotEnabled && VaultHook.isEnabled()) {
				moneyPrize = moneyPot;
				if (m != null)
					switch (m.getAction()) {
						case MULTIPLY_POT_AND_DISTRIBUTE:
							moneyPrize *= m.getAmount();
							break;
						case ADD_TO_POT_AND_DISTRIBUTE:
							moneyPrize += m.getAmount();
							break;
						default:
							break;
					}
				resetMoneyPot();
			}
			ItemList itemPrize = new ItemList();
			if (itemPotEnabled) {
				itemPrize = itemPot.clone();
				if (i != null)
					switch (i.getAction()) {
						case ADD_TO_POT_AND_DISTRIBUTE:
							itemPrize.addAll(i.getItems());
							break;
						case DOUBLE_POT_ITEMS_AND_DISTRIBUTE:
							itemPrize.doubleAmounts();
							break;
						default:
							break;
					}
				resetItemPot();
			}
			handleWin(moneyPrize, itemPrize, true);
		} else if (m != null) {
			double moneyPrize = moneyPot;
			switch (m.getAction()) {
				case MULTIPLY_POT_AND_DISTRIBUTE:
					moneyPrize *= m.getAmount();
					break;
				case ADD_TO_POT_AND_DISTRIBUTE:
					moneyPrize += m.getAmount();
					break;
				case DISTRIBUTE_INDEPENDANT_MONEY:
					moneyPrize = m.getAmount();
					break;
				default:
					break;
			}
			if (m.getAction() != Action.DISTRIBUTE_INDEPENDANT_MONEY)
				resetMoneyPot();
			handleWin(moneyPrize, new ItemList(), false);
		} else if (i != null) {
			ItemList itemPrize = itemPot.clone();
			switch (i.getAction()) {
				case ADD_TO_POT_AND_DISTRIBUTE:
					itemPrize.addAll(i.getItems());
					break;
				case DOUBLE_POT_ITEMS_AND_DISTRIBUTE:
					itemPrize.doubleAmounts();
					break;
				case DISTRIBUTE_INDEPENDANT_ITEMS:
					itemPrize = i.getItems();
					break;
				default:
					break;
			}
			if (i.getAction() != Action.DISTRIBUTE_INDEPENDANT_ITEMS)
				resetItemPot();
			handleWin(0, itemPrize, false);
		} else {
			statistic.getObject(Type.LOST_SPINS).increaseValue(1);
			statistic.saveToFile();
			Player u = getUser();
			PlayerStatistic s = plugin.statisticManager.getStatistic(u, true);
			s.getObject(Type.LOST_SPINS).increaseValue(1);
			s.saveToFile();
			playLoseSounds();
			u.sendMessage(plugin.messageManager.slot_machine_lost());
		}
		deactivate(false);
	}

	public void destruct() {
		deactivate();
		design.destruct(center.getBukkitLocation(), initialDirection);
		instanceReader.deleteFile();
		statistic.deleteFile();
		configReader.deleteConfig();
	}

	@Override
	public void rebuild() {
		deactivate();
		super.rebuild();
		broken = false;
	}

	@Override
	public void move(BlockFace b, int amount) throws Exception {
		deactivate();
		super.move(b, amount);
	}

	public boolean isBroken() {
		return this.broken;
	}

	public String getUserName() {
		return this.userName;
	}

	public Player getUser() {
		return userName == null ? null : Bukkit.getPlayerExact(userName);
	}

	public boolean isUser(Player p) {
		return userName == null ? false : p.getName().equals(userName);
	}

	public long getLockEnd() {
		return this.lockEnd;
	}

	public int getRemainingLockTime() {
		return (int) (lockEnd - System.currentTimeMillis()) / 1000;
	}

	public boolean isLockExpired() {
		return System.currentTimeMillis() > lockEnd;
	}

	public boolean isActive() {
		return this.active;
	}

	public boolean isHalted() {
		return this.halted;
	}

	public boolean isPermittedToHalt(Player p) {
		return active && !halted && !automaticHaltEnabled && isUser(p);
	}
}