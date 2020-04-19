package com.darkblade12.itemslotmachine.design;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.darkblade12.itemslotmachine.manager.Manager;
import com.darkblade12.itemslotmachine.nameable.NameGenerator;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.util.Cuboid;
import com.darkblade12.itemslotmachine.util.ItemBuilder;
import com.darkblade12.itemslotmachine.util.SafeLocation;

public final class DesignManager extends Manager implements NameGenerator {
    private ItemStack wand;
    private NameableComparator<Design> comparator;
    private NameableList<Design> designs;
    private Map<String, SafeLocation[]> selections;

    public DesignManager(ItemSlotMachine plugin) {
        super(plugin);
        onInitialize();
    }

    @Override
    public boolean onInitialize() {
        String wandName = plugin.messageManager.design_wand_name();
        String[] wandLore = plugin.messageManager.design_wand_lore();
        wand = new ItemBuilder().withMaterial(Material.BONE).withName(wandName).withLore(wandLore).build();
        comparator = new NameableComparator<Design>(Settings.getRawDesignName());
        loadDesigns();
        selections = new HashMap<String, SafeLocation[]>();
        registerEvents();
        return true;
    }

    @Override
    public void onDisable() {
        unregisterAll();
    }

    @Override
    public String generateName() {
        Set<Integer> used = new HashSet<Integer>();
        for (String name : getNames())
            if (name.contains(Settings.getRawDesignName()))
                try {
                    used.add(Integer.parseInt(name.replace(Settings.getRawDesignName(), "")));
                } catch (Exception e) {
                    /* custom ids are ignored */
                }
        int n = 1;
        while (used.contains(n))
            n++;
        return Settings.getDefaultDesignName().replace("<num>", Integer.toString(n));
    }

    private void sort() {
        Collections.sort(designs, comparator);
    }

    private Design loadDefaultDesign() {
        try {
            InputStream stream = plugin.getResource("defaultDesign.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line + "\n");
            }

            reader.close();
            return Design.fromString(json.toString());
        } catch (Exception e) {
            plugin.l.warning("Failed to load default design! Cause: " + e.getMessage());

            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void loadDesigns() {
        designs = new NameableList<Design>(true);
        Design defDesign = loadDefaultDesign();

        if (defDesign != null) {
            designs.add(defDesign);
        }

        for (String name : getNames())
            try {
                designs.add(Design.fromFile(name));
            } catch (Exception e) {
                plugin.l.warning("Failed to load design '" + name + "'! Cause: " + e.getMessage());

                if (Settings.isDebugModeEnabled()) {
                    e.printStackTrace();
                }
            }
        sort();
        int amount = designs.size();
        plugin.l.info(amount + " design" + (amount == 1 ? "" : "s") + " loaded.");
    }

    public void register(Design d) {
        designs.add(d);
        sort();
        d.saveToFile();
    }

    public void unregister(Design d) {
        designs.remove(d.getName());
        sort();
        d.deleteFile();
    }

    private void selectPosition(Player p, Location l, boolean first) {
        SafeLocation[] s = getSelection(p);
        s[first ? 0 : 1] = SafeLocation.fromBukkitLocation(l);
        selections.put(p.getName(), s);
    }

    @Override
    public Set<String> getNames() {
        Set<String> names = new HashSet<String>();
        if (Design.DIRECTORY.exists() && Design.DIRECTORY.isDirectory())
            for (File f : Design.DIRECTORY.listFiles()) {
                String name = f.getName();
                if (name.endsWith(".json"))
                    names.add(name.replace(".json", ""));
            }
        return names;
    }

    public ItemStack getWand() {
        return wand.clone();
    }

    public List<Design> getDesigns() {
        return Collections.unmodifiableList(designs);
    }

    public Design getDesign(String name) {
        return designs.get(name);
    }

    public boolean hasDesign(String name) {
        return designs.contains(name);
    }

    public int getDesignAmount() {
        return designs.size();
    }

    private SafeLocation[] getSelection(Player p) {
        String name = p.getName();
        return selections.containsKey(name) ? selections.get(name) : new SafeLocation[2];
    }

    public Cuboid getValidSelection(Player p) {
        SafeLocation[] s = getSelection(p);
        try {
            return new Cuboid(s[0].getBukkitLocation(), s[1].getBukkitLocation());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasValidSelection(Player p) {
        return getValidSelection(p) != null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (event.getHand() == EquipmentSlot.OFF_HAND
                || action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player p = event.getPlayer();

        if (p.getInventory().getItemInMainHand().isSimilar(wand)) {
            event.setCancelled(true);

            Location l = event.getClickedBlock().getLocation();
            boolean first = action == Action.LEFT_CLICK_BLOCK;
            selectPosition(p, l, first);

            String message;

            if (first) {
                message = plugin.messageManager.design_wand_first_position_selected(l.getBlockX(), l.getBlockY(), l.getBlockZ(),
                                                                                    l.getWorld().getName());
            } else {
                message = plugin.messageManager.design_wand_second_position_selected(l.getBlockX(), l.getBlockY(), l.getBlockZ(),
                                                                                     l.getWorld().getName());
            }

            p.sendMessage(message);
        }
    }
}