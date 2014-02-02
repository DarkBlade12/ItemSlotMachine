package com.darkblade12.itemslotmachine.coin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.item.ItemFactory;
import com.darkblade12.itemslotmachine.manager.Manager;
import com.darkblade12.itemslotmachine.safe.SafeLocation;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.sign.SignUpdater;

@SuppressWarnings("deprecation")
public final class CoinManager extends Manager {
	private ItemStack coin;
	private Map<String, SafeLocation> lastShop;
	private Map<String, Integer> shopCoins;
	private BukkitTask task;

	public CoinManager(ItemSlotMachine plugin) {
		super(plugin);
		onInitialize();
	}

	@Override
	public boolean onInitialize() {
		coin = Settings.isCommonCoinItemEnabled() ? Settings.getCoinItem() : ItemFactory.setNameAndLore(Settings.getCoinItem(), plugin.messageManager.coin_name(), plugin.messageManager.coin_lore());
		lastShop = new ConcurrentHashMap<String, SafeLocation>();
		shopCoins = new HashMap<String, Integer>();
		task = new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<String, SafeLocation> e : lastShop.entrySet()) {
					String name = e.getKey();
					SafeLocation s = e.getValue();
					Player p = Bukkit.getPlayerExact(name);
					if (p == null) {
						resetShop(name);
					} else if (s.distance(p.getLocation()) > 8) {
						updateShop(p, s.getBukkitLocation(), 1);
						resetShop(name);
					}
				}
			}
		}.runTaskTimer(plugin, 10, 10);
		registerEvents();
		return true;
	}

	@Override
	public void onDisable() {
		task.cancel();
		unregisterAll();
	}

	public double calculatePrice(int coins) {
		return coins * Settings.getCoinPrice();
	}

	private void updateShop(Player p, Location l, int coins) {
		String name = p.getName();
		lastShop.put(name, SafeLocation.fromBukkitLocation(l));
		shopCoins.put(name, coins);
		SignUpdater.updateSign(p, l,
				new String[] { plugin.messageManager.sign_coin_shop_header(), plugin.messageManager.sign_coin_shop_coins(coins), plugin.messageManager.sign_coin_shop_price(calculatePrice(coins)),
						plugin.messageManager.sign_coin_shop_spacer() }, 2);
	}

	private void resetLastShop(String name) {
		lastShop.remove(name);
	}

	private void resetShopCoins(String name) {
		lastShop.remove(name);
	}

	private void resetShopCoins(Player p) {
		resetShopCoins(p.getName());
	}

	private void resetShop(String name) {
		resetLastShop(name);
		resetShopCoins(name);
	}

	public ItemStack getCoin(int amount) {
		ItemStack i = coin.clone();
		i.setAmount(amount);
		return i;
	}

	public boolean isCoin(ItemStack i) {
		return i.isSimilar(coin);
	}

	private SafeLocation getLastShop(Player p) {
		String name = p.getName();
		return lastShop.containsKey(name) ? lastShop.get(name) : null;
	}

	private int getShopCoins(Player p) {
		String name = p.getName();
		return shopCoins.containsKey(name) ? shopCoins.get(name) : 1;
	}

	private boolean isShop(Sign s) {
		return s.getLine(0).equals(plugin.messageManager.sign_coin_shop_header());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[CoinShop]")) {
			String[] lines = SignUpdater.validateLines(
					new String[] { plugin.messageManager.sign_coin_shop_header(), plugin.messageManager.sign_coin_shop_coins(1), plugin.messageManager.sign_coin_shop_price(Settings.getCoinPrice()),
							plugin.messageManager.sign_coin_shop_spacer() }, 2);
			event.setLine(0, lines[0]);
			event.setLine(1, lines[1]);
			event.setLine(2, lines[2]);
			event.setLine(3, lines[3]);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		int previous = event.getPreviousSlot();
		int next = event.getNewSlot();
		if (next != previous) {
			Player p = event.getPlayer();
			Sign s;
			try {
				s = (Sign) p.getTargetBlock(null, 6).getState();
				if (!isShop(s))
					return;
			} catch (Exception e) {
				return;
			}
			Location l = s.getLocation();
			SafeLocation last = getLastShop(p);
			if (last != null && !last.noDistance(l)) {
				updateShop(p, last.getBukkitLocation(), 1);
				resetShopCoins(p);
			}
			int coins = getShopCoins(p);
			int amount = p.isSneaking() ? 10 : 1;
			if (next > previous)
				coins += next == 8 && previous == 0 ? coins + amount <= 100 ? amount : 100 - coins : coins - amount > 0 ? -amount : -coins + 1;
			else
				coins += next == 0 && previous == 8 ? coins - amount > 0 ? -amount : -coins + 1 : coins + amount <= 100 ? amount : 100 - coins;
			updateShop(p, l, coins);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign s;
			try {
				s = (Sign) event.getClickedBlock().getState();
			} catch (Exception e) {
				return;
			}
			if (isShop(s)) {
				event.setCancelled(true);
				Player p = event.getPlayer();
				plugin.coinCommandHandler.getCommand("purchase").execute(plugin, p, "coin", new String[] { Integer.toString(getShopCoins(p)) });
				p.updateInventory();
			}
		}
	}
}