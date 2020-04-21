package com.darkblade12.itemslotmachine.coin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.manager.Manager;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.util.ItemBuilder;
import com.darkblade12.itemslotmachine.util.SafeLocation;

public final class CoinManager extends Manager {
    private ItemStack coin;
    private Map<UUID, ShopInfo> lastShop;
    private BukkitTask task;

    public CoinManager(ItemSlotMachine plugin) {
        super(plugin);
        onInitialize();
    }

    public static String[] validateLines(String[] lines, int... splitLines) {
        if (lines.length > 4) {
            throw new IllegalArgumentException("There cannot be more than 4 lines on a sign");
        }

        for (int index : splitLines) {
            if (index >= 0 && index < lines.length - 1) {
                String line = lines[index];
                if (line.length() > 15) {
                    String[] split = line.split(" ");
                    lines[index] = split[0];
                    lines[index + 1] = split[1];
                }
            }
        }

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            lines[i] = line.length() > 15 ? line.substring(0, 15) : line;
        }

        return lines;
    }

    @Override
    public boolean onInitialize() {
        String coinName = plugin.messageManager.coin_name();
        String[] coinLore = plugin.messageManager.coin_lore();
        ItemBuilder builder = new ItemBuilder().withMaterial(Settings.getCoinMaterial());
        if (!Settings.isCommonCoinItemEnabled()) {
            builder.withName(coinName).withLore(coinLore);
        }
        coin = builder.build();
        
        lastShop = new ConcurrentHashMap<UUID, ShopInfo>();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Entry<UUID, ShopInfo> e : lastShop.entrySet()) {
                    UUID id = e.getKey();
                    Player player = Bukkit.getPlayer(id);

                    if (player == null) {
                        resetLastShop(id);
                    } else {
                        SafeLocation shop = e.getValue().getLocation();
                        Location current = player.getLocation();

                        if (!shop.getWorldName().equals(current.getWorld().getName()) || shop.distanceSquared(current) > 64) {
                            updateShop(player, shop.getBukkitLocation(), 1);
                            resetLastShop(id);
                        }
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

    private void updateShop(Player player, Location signLocation, int coins) {
        BlockState state = signLocation.getBlock().getState();
        if (!(state instanceof Sign)) {
            return;
        }

        ShopInfo info = getLastShop(player);
        SafeLocation location = SafeLocation.fromBukkitLocation(signLocation);
        if (info == null) {
            info = new ShopInfo(location, coins);
            lastShop.put(player.getUniqueId(), info);
        } else {
            info.setLocation(location);
            info.setCoins(coins);
        }

        String[] lines = new String[] { plugin.messageManager.sign_coin_shop_header(),
                                        plugin.messageManager.sign_coin_shop_coins(coins),
                                        plugin.messageManager.sign_coin_shop_price(calculatePrice(coins)),
                                        plugin.messageManager.sign_coin_shop_spacer() };
        lines = validateLines(lines, 2);
        player.sendSignChange(signLocation, lines);
    }

    private void resetLastShop(UUID id) {
        lastShop.remove(id);
    }

    public ItemStack getCoin(int amount) {
        ItemStack item = coin.clone();
        item.setAmount(amount);
        return item;
    }

    public boolean isCoin(ItemStack item) {
        return item.isSimilar(coin);
    }

    private ShopInfo getLastShop(Player player) {
        return lastShop.getOrDefault(player.getUniqueId(), null);
    }

    private int getShopCoins(Player player) {
        ShopInfo info = getLastShop(player);
        return info == null ? 1 : info.getCoins();
    }

    private boolean isShop(Sign sign) {
        return sign.getLine(0).equals(plugin.messageManager.sign_coin_shop_header());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase("[CoinShop]")) {
            return;
        }

        String[] lines = new String[] { plugin.messageManager.sign_coin_shop_header(),
                                        plugin.messageManager.sign_coin_shop_coins(1),
                                        plugin.messageManager.sign_coin_shop_price(Settings.getCoinPrice()),
                                        plugin.messageManager.sign_coin_shop_spacer() };
        lines = validateLines(lines, 2);
        event.setLine(0, lines[0]);
        event.setLine(1, lines[1]);
        event.setLine(2, lines[2]);
        event.setLine(3, lines[3]);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        int previous = event.getPreviousSlot();
        int next = event.getNewSlot();
        Player player = event.getPlayer();
        Block target = player.getTargetBlockExact(6);
        if (next == previous || target == null) {
            return;
        }


        BlockState state = target.getState();
        if (!(state instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) state;
        if (!isShop(sign)) {
            return;
        }

        Location location = sign.getLocation();
        ShopInfo lastShop = getLastShop(player);
        if (lastShop != null && !lastShop.getLocation().noDistance(location)) {
            updateShop(player, lastShop.getBukkitLocation(), 1);
        }

        int coins = lastShop == null ? 1 : lastShop.getCoins();
        int amount = player.isSneaking() ? 10 : 1;
        boolean increase = next < previous && (previous != 8 || next != 0) || previous == 0 && next == 8;
        coins += increase ? amount : -amount;
        if (coins < 1) {
            coins = 1;
        } else if (coins > 100) {
            coins = 100;
        }

        updateShop(player, location, coins);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getHand() == EquipmentSlot.OFF_HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK || block == null) {
            return;
        }

        BlockState state = block.getState();
        if (!(state instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) state;
        if (!isShop(sign)) {
            return;
        }

        event.setCancelled(true);
        Player p = event.getPlayer();
        ICommand purchase = plugin.coinCommandHandler.getCommand("purchase");
        purchase.execute(plugin, p, "coin", new String[] { Integer.toString(getShopCoins(p)) });
    }
}