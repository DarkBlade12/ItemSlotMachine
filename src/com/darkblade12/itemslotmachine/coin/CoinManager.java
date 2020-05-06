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
import com.darkblade12.itemslotmachine.Settings;
import com.darkblade12.itemslotmachine.core.Manager;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.util.ItemBuilder;
import com.darkblade12.itemslotmachine.util.MessageUtils;
import com.darkblade12.itemslotmachine.util.SafeLocation;

public final class CoinManager extends Manager<ItemSlotMachine> {
    private Map<UUID, ShopInfo> lastShop;
    private ItemStack coin;
    private CommandBase<ItemSlotMachine> purchaseCommand;
    private BukkitTask task;

    public CoinManager(ItemSlotMachine plugin) {
        super(plugin);
        lastShop = new ConcurrentHashMap<UUID, ShopInfo>();
    }

    @Override
    public void onEnable() {
        Settings settings = plugin.getSettings();
        ItemBuilder builder = new ItemBuilder().withType(settings.getCoinType());
        if (!settings.getUseCommonCoinItem()) {
            String coinName = plugin.formatMessage(Message.COIN_ITEM_NAME);
            String[] coinLore = plugin.formatMessage(Message.COIN_ITEM_LORE).split("\n");
            builder.withName(coinName).withLore(coinLore);
        }
        coin = builder.build();

        purchaseCommand = plugin.coinCommandHandler.getCommand("purchase");
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Entry<UUID, ShopInfo> entry : lastShop.entrySet()) {
                    UUID id = entry.getKey();
                    Player player = Bukkit.getPlayer(id);
                    if (player == null) {
                        resetLastShop(id);
                        continue;
                    }

                    SafeLocation shop = entry.getValue().getLocation();
                    Location current = player.getLocation();
                    if (!shop.getWorldName().equals(current.getWorld().getName()) || shop.distanceSquared(current) > 64) {
                        updateShop(player, shop.getBukkitLocation(), 1);
                        resetLastShop(id);
                    }
                }
            }
        }.runTaskTimer(plugin, 10, 10);
        registerEvents();
    }

    @Override
    public void onDisable() {
        task.cancel();
        unregisterEvents();
    }

    public double calculatePrice(int coins) {
        return coins * plugin.getSettings().getCoinPrice();
    }

    private String[] getLines(int coins) {
        double price = calculatePrice(coins);
        String[] lines = { plugin.formatMessage(Message.SIGN_SHOP_HEADER), plugin.formatMessage(Message.SIGN_SHOP_COINS, coins),
                           plugin.formatMessage(Message.SIGN_SHOP_PRICE, price), plugin.formatMessage(Message.SIGN_SHOP_SPACER) };
        return lines;
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

        String[] lines = MessageUtils.prepareSignLines(getLines(coins), 2);
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

    public ItemStack getCoin() {
        return getCoin(1);
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
        return sign.getLine(0).equals(plugin.formatMessage(Message.SIGN_SHOP_HEADER));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase("[CoinShop]") || !Permission.SHOP_CREATE.has(event.getPlayer())) {
            return;
        }

        String[] lines = MessageUtils.prepareSignLines(getLines(1), 2);
        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, lines[i]);
        }
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
        if (lastShop != null && !lastShop.getLocation().equals(location)) {
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
        Player player = event.getPlayer();
        purchaseCommand.execute(plugin, player, "coin", new String[] { Integer.toString(getShopCoins(player)) });
    }
}