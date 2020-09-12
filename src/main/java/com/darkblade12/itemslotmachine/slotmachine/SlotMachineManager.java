package com.darkblade12.itemslotmachine.slotmachine;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.Manager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.settings.InvalidValueException;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.darkblade12.itemslotmachine.util.ItemUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class SlotMachineManager extends Manager<ItemSlotMachine> {
    private final NameableList<SlotMachine> slots;
    private NameableComparator<SlotMachine> defaultComparator;

    public SlotMachineManager(ItemSlotMachine plugin) {
        super(plugin, new File(plugin.getDataFolder(), "slot machines"));
        slots = new NameableList<SlotMachine>();
    }

    @Override
    protected void onEnable() {
        defaultComparator = new NameableComparator<SlotMachine>(plugin.getSettings().getSlotMachineNamePattern());
        loadSlotMachines();
    }

    @Override
    protected void onDisable() {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).stop(true);
        }
    }

    public void loadSlotMachines() {
        slots.clear();
        for (File file : FileUtils.getFiles(dataDirectory, SlotMachine.FILE_EXTENSION)) {
            try {
                slots.add(SlotMachine.fromFile(plugin, file));
            } catch (JsonIOException | JsonSyntaxException | InvalidValueException | IOException ex) {
                plugin.logException("Failed to load slot machine file {1}: {0}", ex, file.getName());
            }
        }
        int count = slots.size();
        plugin.logInfo(count + " slot machine" + (count == 1 ? "" : "s") + " loaded.");
    }

    public void register(SlotMachine slot) {
        slots.add(slot);
    }

    public void unregister(SlotMachine slot) {
        slots.remove(slot.getName());
        slot.delete();
    }

    public boolean hasFile(String name) {
        for (String fileName : getFileNames(true)) {
            if (fileName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
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
        return getSlotMachines().getNames();
    }

    public NameableList<SlotMachine> getSlotMachines() {
        NameableList<SlotMachine> clone = new NameableList<SlotMachine>(slots);
        clone.sort(defaultComparator);
        return clone;
    }

    public SlotMachine getSlotMachine(String name) {
        return slots.get(name);
    }

    public SlotMachine getSlotMachine(Location location) {
        for (int i = 0; i < slots.size(); i++) {
            SlotMachine slot = slots.get(i);
            if (slot.isInsideRegion(location)) {
                return slot;
            }
        }
        return null;
    }

    private SlotMachine getInteractedSlotMachine(Location location) {
        for (int i = 0; i < slots.size(); i++) {
            SlotMachine slot = slots.get(i);
            if (slot.isInteraction(location)) {
                return slot;
            }
        }
        return null;
    }

    public boolean hasSlotMachine(String name) {
        return slots.containsName(name);
    }

    public int getSlotMachineCount() {
        return slots.size();
    }

    private int getSpinningCount(Player player) {
        int count = 0;
        for (int i = 0; i < slots.size(); i++) {
            SlotMachine slot = slots.get(i);
            if (slot.isUser(player) && slot.isSpinning()) {
                count++;
            }
        }
        return count;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        SlotMachine slot = getSlotMachine(event.getBlock().getLocation());
        if (slot == null || slot.hasModifyPermission(player)) {
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
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        Player player = event.getPlayer();
        Location clickedLoc;
        SlotMachine slot;
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                clickedLoc = event.getClickedBlock().getLocation();
                slot = getInteractedSlotMachine(clickedLoc);
                if (slot == null || !slot.isStoppable(player)) {
                    return;
                }

                event.setCancelled(true);
                slot.stop();
                break;
            case RIGHT_CLICK_BLOCK:
                clickedLoc = event.getClickedBlock().getLocation();
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

                boolean holdingUseItem = !hand.getType().isBlock() || hand.getType() == Material.AIR;
                boolean holdingCoin = plugin.coinManager.isCoin(hand);
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
                    int current = ItemUtils.getTotalAmount(player, plugin.coinManager.getCoin());
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
                return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (int i = 0; i < slots.size(); i++) {
            SlotMachine slot = slots.get(i);
            if (slot.isUser(player)) {
                slot.stop(true);
            }
        }
    }
}
