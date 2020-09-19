package com.darkblade12.itemslotmachine.slotmachine;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.coin.CoinManager;
import com.darkblade12.itemslotmachine.design.DesignIncompleteException;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.plugin.Manager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.settings.InvalidValueException;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.darkblade12.itemslotmachine.util.ItemUtils;
import com.google.gson.JsonParseException;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SlotMachineManager extends Manager<ItemSlotMachine> {
    private final List<SlotMachine> slots;
    private NameableComparator<SlotMachine> comparator;

    public SlotMachineManager(ItemSlotMachine plugin) {
        super(plugin, new File(plugin.getDataFolder(), "slot machines"));
        slots = new ArrayList<>();
    }

    @Override
    protected void onEnable() {
        comparator = new NameableComparator<>(plugin.getSettings().getSlotMachineNamePattern());
        loadSlotMachines();
    }

    @Override
    protected void onDisable() {
        for (SlotMachine slot : slots) {
            slot.stop(true);
        }
    }

    public void loadSlotMachines() {
        slots.clear();

        for (File file : FileUtils.getFiles(dataDirectory, SlotMachine.FILE_EXTENSION)) {
            try {
                slots.add(SlotMachine.fromFile(plugin, file));
            } catch (JsonParseException | InvalidValueException | IOException | DesignIncompleteException e) {
                plugin.logException(e, "Failed to load slot machine file %s!", file.getName());
            }
        }

        int count = slots.size();
        plugin.logInfo(count + " slot machine" + (count == 1 ? "" : "s") + " loaded.");
    }

    public void register(SlotMachine slot) {
        slots.add(slot);
    }

    public void unregister(SlotMachine slot) throws IOException {
        slot.delete();
        slots.remove(slot);
    }

    public String generateName() {
        return Nameable.generateName(getFileNames(true), plugin.getSettings().getSlotMachineNamePattern());
    }

    public List<String> getFileNames(boolean stripExtension) {
        return FileUtils.getFileNames(dataDirectory, stripExtension, SlotMachine.FILE_EXTENSION);
    }

    public List<String> getFileNames() {
        return getFileNames(false);
    }

    public List<String> getNames() {
        return slots.stream().sorted(comparator).map(SlotMachine::getName).collect(Collectors.toList());
    }

    public List<SlotMachine> getSlotMachines() {
        return slots.stream().sorted(comparator).collect(Collectors.toList());
    }

    public SlotMachine getSlotMachine(String name) {
        return slots.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public SlotMachine getSlotMachine(Location location) {
        return slots.stream().filter(s -> s.isInsideRegion(location)).findFirst().orElse(null);
    }

    private SlotMachine getInteractedSlotMachine(Location location) {
        return slots.stream().filter(s -> s.isInteraction(location)).findFirst().orElse(null);
    }

    public boolean hasSlotMachine(String name) {
        return slots.stream().anyMatch(s -> s.getName().equalsIgnoreCase(name));
    }

    private int getSpinningCount(Player player) {
        return (int) slots.stream().filter(s -> s.isSpinning() && s.isUser(player)).count();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        SlotMachine slot = getSlotMachine(event.getBlock().getLocation());
        if (player == null || slot == null || slot.hasModifyPermission(player)) {
            return;
        }

        event.setCancelled(true);
        plugin.sendMessage(player, Message.SLOT_MACHINE_MODIFY_NOT_ALLOWED, slot.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingBreak(HangingBreakEvent event) {
        SlotMachine slot = getSlotMachine(event.getEntity().getLocation());
        if (slot == null) {
            return;
        } else if (!(event instanceof HangingBreakByEntityEvent)) {
            event.setCancelled(true);
            return;
        }

        Entity remover = ((HangingBreakByEntityEvent) event).getRemover();
        if (!(remover instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) remover;
        if (slot.hasModifyPermission(player)) {
            return;
        }

        event.setCancelled(true);
        plugin.sendMessage(player, Message.SLOT_MACHINE_MODIFY_NOT_ALLOWED, slot.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlotMachine slot = getSlotMachine(event.getBlock().getLocation());
        if (slot == null || slot.hasModifyPermission(player)) {
            return;
        }

        event.setCancelled(true);
        plugin.sendMessage(player, Message.SLOT_MACHINE_MODIFY_NOT_ALLOWED, slot.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        SlotMachine slot = getSlotMachine(event.getBlock().getLocation());
        if (slot == null) {
            slot = getSlotMachine(event.getBlockAgainst().getLocation());
        }

        if (slot == null || slot.hasModifyPermission(player)) {
            return;
        }

        event.setCancelled(true);
        plugin.sendMessage(player, Message.SLOT_MACHINE_MODIFY_NOT_ALLOWED, slot.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Hanging)) {
            return;
        }

        SlotMachine slot = getSlotMachine(entity.getLocation());
        if (slot == null || slot.hasModifyPermission(player)) {
            return;
        }

        event.setCancelled(true);
        plugin.sendMessage(player, Message.SLOT_MACHINE_MODIFY_NOT_ALLOWED, slot.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Hanging)) {
            return;
        }

        SlotMachine slot = getSlotMachine(entity.getLocation());
        if (slot == null) {
            return;
        } else if (!(event instanceof EntityDamageByEntityEvent)) {
            event.setCancelled(true);
            return;
        }

        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
        if (!(damager instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) damager;
        if (slot.hasModifyPermission(player)) {
            return;
        }

        event.setCancelled(true);
        plugin.sendMessage(player, Message.SLOT_MACHINE_MODIFY_NOT_ALLOWED, slot.getName());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        Player player = event.getPlayer();
        Location clickedLoc = clickedBlock.getLocation();
        SlotMachine slot;
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                slot = getInteractedSlotMachine(clickedLoc);
                if (slot == null || !slot.isStoppable(player)) {
                    return;
                }

                event.setCancelled(true);
                slot.stop();
                break;
            case RIGHT_CLICK_BLOCK:
                ItemStack hand = player.getInventory().getItemInMainHand();
                slot = getSlotMachine(clickedLoc);
                if (slot == null) {
                    return;
                }
                String name = slot.getName();

                if (hand.getType() == Material.WATER_BUCKET || hand.getType() == Material.LAVA_BUCKET) {
                    if (!slot.hasModifyPermission(player)) {
                        event.setCancelled(true);
                        plugin.sendMessage(player, Message.SLOT_MACHINE_MODIFY_NOT_ALLOWED, name);
                    }
                    return;
                }

                CoinManager coinManager = plugin.getManager(CoinManager.class);
                boolean holdingUseItem = !hand.getType().isBlock() || hand.getType() == Material.AIR;
                boolean holdingCoin = coinManager.isCoin(hand);
                if (holdingUseItem && !holdingCoin && Permission.SLOT_INSPECT.test(player)) {
                    plugin.sendMessage(player, Message.SLOT_MACHINE_INSPECTED, name);
                    return;
                } else if (!slot.isInteraction(clickedLoc) || !holdingCoin) {
                    return;
                }

                event.setCancelled(true);
                if (!slot.hasUsePermission(player)) {
                    plugin.sendMessage(player, Message.SLOT_MACHINE_USE_NOT_ALLOWED, name);
                    return;
                }

                if (slot.isBroken()) {
                    plugin.sendMessage(player, Message.SLOT_MACHINE_BROKEN, name);
                    return;
                }

                if (slot.isSpinning()) {
                    plugin.sendMessage(player, Message.SLOT_MACHINE_STILL_SPINNING);
                    return;
                }

                if (slot.getSettings().lockTime > 0 && !slot.isLockExpired() && !slot.isUser(player)) {
                    plugin.sendMessage(player, Message.SLOT_MACHINE_LOCKED, slot.getUserName(), slot.getRemainingLockTime());
                    return;
                }

                if (player.getGameMode() == GameMode.CREATIVE && !slot.getSettings().allowCreative) {
                    plugin.sendMessage(player, Message.SLOT_MACHINE_NO_CREATIVE);
                    return;
                }

                if (!slot.hasEnoughCoins(player)) {
                    String singular = plugin.formatMessage(Message.WORD_COIN_SINGULAR);
                    String plural = plugin.formatMessage(Message.WORD_COIN_PLURAL);
                    int required = slot.getSettings().coinAmount;
                    String requiredCoins = required == 1 ? singular : plural;
                    int current = ItemUtils.getTotalAmount(player, coinManager.getCoin());
                    String currentCoins = current == 1 ? singular : plural;
                    plugin.sendMessage(player, Message.SLOT_MACHINE_NOT_ENOUGH_COINS, required, requiredCoins, current,
                                       currentCoins);
                    return;
                }

                int useLimit = plugin.getSettings().getSlotMachineUseLimit();
                if (useLimit > 0 && getSpinningCount(player) + 1 > useLimit) {
                    plugin.sendMessage(player, Message.SLOT_MACHINE_USE_LIMITED, useLimit);
                    return;
                }

                slot.spin(player);
                break;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (SlotMachine slot : slots) {
            if (slot.isUser(player)) {
                slot.stop(true);
            }
        }
    }
}
