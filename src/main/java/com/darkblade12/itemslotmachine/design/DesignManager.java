package com.darkblade12.itemslotmachine.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.Manager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.util.Cuboid;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.darkblade12.itemslotmachine.util.ItemBuilder;
import com.darkblade12.itemslotmachine.util.SafeLocation;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public final class DesignManager extends Manager<ItemSlotMachine> {
    private final List<Design> designs;
    private NameableComparator<Design> comparator;
    private final Map<UUID, SafeLocation[]> selections;
    private ItemStack wand;

    public DesignManager(ItemSlotMachine plugin) {
        super(plugin, new File(plugin.getDataFolder(), "designs"));
        designs = new ArrayList<>();
        selections = new HashMap<>();
    }

    @Override
    protected void onEnable() {
        comparator = new NameableComparator<>(plugin.getSettings().getDesignNamePattern());
        String wandName = plugin.formatMessage(Message.DESIGN_WAND_NAME);
        String[] wandLore = plugin.formatMessage(Message.DESIGN_WAND_LORE).split("\n");
        wand = new ItemBuilder().withType(Material.BONE).withName(wandName).withLore(wandLore).build();

        loadDesigns();
    }

    @Override
    protected void onDisable() {
        designs.clear();
        selections.clear();
    }

    private Design loadDefaultDesign() {
        try {
            return FileUtils.readJson(plugin, Design.DEFAULT_FILE, Design.class);
        } catch (IOException | JsonParseException ex) {
            plugin.logException("Failed to load default design: {0}", ex);
            return null;
        }
    }

    private void loadDesigns() {
        designs.clear();
        Design defaultDesign = loadDefaultDesign();
        if (defaultDesign != null) {
            designs.add(defaultDesign);
        }

        for (File file : FileUtils.getFiles(dataDirectory, Design.FILE_EXTENSION)) {
            try {
                Design design = Design.fromFile(file);
                if (!design.getRegion().isValid()) {
                    design = convertDesign(file);
                }

                designs.add(design);
            } catch (IOException | JsonParseException | DesignIncompleteException ex) {
                plugin.logException("Failed to load design file {1}: {0}", ex, file.getName());
            }
        }

        int amount = designs.size();
        plugin.logInfo("{0} design{1} loaded.", amount, amount == 1 ? "" : "s");
    }

    private Design convertDesign(File file) throws IOException, JsonParseException, DesignIncompleteException {
        JsonObject obj = FileUtils.readJson(file, JsonObject.class);
        JsonObject region = obj.getAsJsonObject("region");
        String firstName = "firstVertice";
        String secondName = "secondVertice";
        JsonObject firstVertex = region.getAsJsonObject(firstName);
        JsonObject secondVertex = region.getAsJsonObject(secondName);
        if (firstVertex.isJsonNull() || secondVertex.isJsonNull()) {
            throw new DesignIncompleteException("The design is missing region vertices.");
        }

        region.add("firstVertex", firstVertex);
        region.add("secondVertex", secondVertex);
        region.remove(firstName);
        region.remove(secondName);

        FileUtils.saveJson(file, obj);
        return FileUtils.GSON.fromJson(obj, Design.class);
    }

    public void register(Design design) {
        try {
            design.saveFile(dataDirectory);
            designs.add(design);
        } catch (IOException ex) {
            plugin.logException("Failed to save design file {1}: {0}", ex, design.getFileName());
        }
    }

    public void unregister(Design design) throws IOException {
        design.deleteFile(dataDirectory);
        String name = design.getName();
        designs.removeIf(d -> d.getName().equalsIgnoreCase(name));
    }

    private void selectPosition(Player player, Location location, boolean first) {
        SafeLocation[] safeLoc = getSelection(player);
        safeLoc[first ? 0 : 1] = SafeLocation.fromBukkitLocation(location);
        selections.put(player.getUniqueId(), safeLoc);
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
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        Location loc = clicked.getLocation();
        boolean first = action == Action.LEFT_CLICK_BLOCK;
        selectPosition(player, loc, first);

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        String worldName = Objects.requireNonNull(loc.getWorld()).getName();
        Message message = first ? Message.DESIGN_WAND_FIRST_SELECTED : Message.DESIGN_WAND_SECOND_SELECTED;
        plugin.sendMessage(player, message, x, y, z, worldName);
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
        return designs.stream().sorted(comparator).map(Design::getName).collect(Collectors.toList());
    }

    public Design getDesign(String name) {
        return designs.stream().filter(d -> d.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean hasDesigns() {
        return !designs.isEmpty();
    }

    public boolean hasDesign(String name) {
        return designs.stream().anyMatch(d -> d.getName().equalsIgnoreCase(name));
    }

    private SafeLocation[] getSelection(Player player) {
        UUID id = player.getUniqueId();
        return selections.containsKey(id) ? selections.get(id) : new SafeLocation[2];
    }

    public Cuboid getSelectionRegion(Player player) {
        SafeLocation[] locations = getSelection(player);
        try {
            return new Cuboid(locations[0].toBukkitLocation(), locations[1].toBukkitLocation());
        } catch (NullPointerException | IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean hasValidSelection(Player player) {
        return getSelectionRegion(player) != null;
    }
}
