package com.darkblade12.itemslotmachine.design;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Manager;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.util.Cuboid;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.darkblade12.itemslotmachine.util.ItemBuilder;
import com.darkblade12.itemslotmachine.util.SafeLocation;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class DesignManager extends Manager<ItemSlotMachine> {
    private final NameableList<Design> designs;
    private NameableComparator<Design> defaultComparator;
    private final Map<UUID, SafeLocation[]> selections;
    private ItemStack wand;

    public DesignManager(ItemSlotMachine plugin) {
        super(plugin, new File(plugin.getDataFolder(), "designs"));
        designs = new NameableList<Design>();
        selections = new HashMap<UUID, SafeLocation[]>();
    }

    @Override
    public void onEnable() {
        defaultComparator = new NameableComparator<Design>(plugin.getSettings().getDesignNamePattern());
        String wandName = plugin.formatMessage(Message.DESIGN_WAND_NAME);
        String[] wandLore = plugin.formatMessage(Message.DESIGN_WAND_LORE).split("\n");
        wand = new ItemBuilder().withMaterial(Material.BONE).withName(wandName).withLore(wandLore).build();
        loadDesigns();
        registerEvents();
    }

    @Override
    public void onDisable() {
        unregisterEvents();
        designs.clear();
        selections.clear();
    }

    private Design loadDefaultDesign() {
        try {
            return FileUtils.readJson(plugin, Design.DEFAULT_FILE, Design.class);
        } catch (IOException | JsonIOException | JsonSyntaxException ex) {
            plugin.logException("Failed to load default design: %c", ex);
            return null;
        }
    }

    public void loadDesigns() {
        designs.clear();
        Design defaultDesign = loadDefaultDesign();
        if (defaultDesign != null) {
            designs.add(defaultDesign);
        }

        for (File file : FileUtils.getFiles(dataDirectory, Design.FILE_EXTENSION)) {
            try {
                Design design = Design.fromFile(file);
                designs.add(design);
            } catch (IOException | JsonIOException | JsonSyntaxException ex) {
                plugin.logException("Failed to load design file '" + file.getName() + "': %c", ex);
            }
        }

        int amount = designs.size();
        plugin.logInfo(amount + " design" + (amount == 1 ? "" : "s") + " loaded.");
    }

    public void register(Design design) {
        try {
            design.saveFile(dataDirectory);
            designs.add(design);
        } catch (IOException ex) {
            plugin.logException("Failed to save design file '" + design.getFileName() + "': %c", ex);
        }
    }

    public void unregister(Design design) throws IOException {
        design.deleteFile(dataDirectory);
        designs.remove(design.getName());
    }

    private void selectPosition(Player player, Location location, boolean first) {
        SafeLocation[] safeLoc = getSelection(player);
        safeLoc[first ? 0 : 1] = SafeLocation.fromBukkitLocation(location);
        selections.put(player.getUniqueId(), safeLoc);
    }

    public ItemStack getWand() {
        return wand.clone();
    }

    public String generateName() {
        return Nameable.generateName(getFileNames(true), plugin.getSettings().getDesignNamePattern());
    }

    public List<String> getFileNames(boolean stripExtension) {
        return FileUtils.getFileNames(dataDirectory, stripExtension, Design.FILE_EXTENSION);
    }

    public List<String> getFileNames() {
        return getFileNames(false);
    }

    public List<String> getNames() {
        return getDesigns().getNames();
    }

    public NameableList<Design> getDesigns() {
        NameableList<Design> clone = new NameableList<>(designs);
        clone.sort(defaultComparator);
        return clone;
    }

    public Design getDesign(String name) {
        return designs.get(name);
    }

    public boolean hasDesign(String name) {
        return designs.containsName(name);
    }

    public int getDesignAmount() {
        return designs.size();
    }

    private SafeLocation[] getSelection(Player player) {
        UUID id = player.getUniqueId();
        return selections.containsKey(id) ? selections.get(id) : new SafeLocation[2];
    }

    public Cuboid getSelectionRegion(Player player) {
        SafeLocation[] locations = getSelection(player);
        try {
            return new Cuboid(locations[0].getBukkitLocation(), locations[1].getBukkitLocation());
        } catch (NullPointerException | IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean hasValidSelection(Player player) {
        return getSelectionRegion(player) != null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (event.getHand() == EquipmentSlot.OFF_HAND
                || action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.getInventory().getItemInMainHand().isSimilar(wand)) {
            return;
        }

        event.setCancelled(true);
        Location loc = event.getClickedBlock().getLocation();
        boolean first = action == Action.LEFT_CLICK_BLOCK;
        selectPosition(player, loc, first);

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        String world = loc.getWorld().getName();
        Message message = first ? Message.DESIGN_WAND_FIRST_SELECTED : Message.DESIGN_WAND_SECOND_SELECTED;
        plugin.sendMessage(player, message, x, y, z, world);
    }
}