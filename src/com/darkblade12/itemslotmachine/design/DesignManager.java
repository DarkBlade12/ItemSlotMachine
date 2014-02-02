package com.darkblade12.itemslotmachine.design;

import java.io.File;
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
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.cuboid.Cuboid;
import com.darkblade12.itemslotmachine.item.ItemFactory;
import com.darkblade12.itemslotmachine.manager.Manager;
import com.darkblade12.itemslotmachine.nameable.NameGenerator;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.safe.SafeLocation;
import com.darkblade12.itemslotmachine.settings.Settings;

public final class DesignManager extends Manager implements NameGenerator {
	private static final String DEFAULT_DESIGN = "default#-3@2@1@126@0, -4@2@4@53@0, -2@3@2@5@0, -4@3@2@139@0, -4@3@1@139@0, -2@1@4@53@6, -1@3@4@134@3, -2@3@1@5@0, 0@1@0@43@0, -3@3@3@5@0, -4@3@3@43@0, -3@3@2@5@0, -4@1@4@134@2, -1@2@1@126@0, -4@2@1@5@0, -3@3@1@5@0, -2@2@1@126@0, -1@2@4@5@0, -1@3@1@5@0, -1@1@4@134@2, -2@2@0@53@6, -3@2@0@53@6, -4@1@3@139@0, -4@3@4@134@3, 0@1@1@139@0, -1@3@2@5@0, -4@2@2@5@0, -1@3@0@98@0, -2@2@4@5@0, -1@2@3@53@6, 0@3@2@139@0, -1@1@0@126@8, -1@3@3@5@0, -4@2@0@98@0, -2@3@3@5@0, 0@3@1@139@0, -3@3@0@98@0, 0@3@0@43@0, -3@1@4@134@2, -3@2@4@5@0, 0@3@3@43@0, -1@2@0@53@6, 0@1@4@134@2, -4@1@1@139@0, -2@3@0@98@0, -4@3@0@43@0, -2@2@3@53@6, 0@2@3@5@0, -3@1@0@126@8, 0@1@3@139@0, -4@2@3@5@0, -2@3@4@53@3, 0@2@2@5@0, -4@1@0@43@0, -2@2@5@126@0, -3@3@4@134@3, 0@2@4@53@1, -4@1@2@139@0, 0@2@0@98@0, 0@1@2@139@0, 0@3@4@134@3, 0@2@1@5@0, -3@2@3@53@6#-1@2@2@NORTH, -2@2@2@NORTH, -3@2@2@NORTH#-2@1@1@68@2#-2@1@0@84@0#0@3@5&-4@1@0#SOUTH";
	private static final File DIRECTORY = new File("plugins/ItemSlotMachine/designs/");
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
		wand = ItemFactory.setNameAndLore(new ItemStack(Material.BONE), plugin.messageManager.design_wand_name(), plugin.messageManager.design_wand_lore());
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

	public void loadDesigns() {
		designs = new NameableList<Design>(true);
		try {
			designs.add(Design.fromString(DEFAULT_DESIGN));
		} catch (Exception e) {
			/* do nothing */
		}
		for (String name : getNames())
			try {
				designs.add(Design.fromFile(name));
			} catch (Exception e) {
				plugin.l.warning("Failed to load design '" + name + "'! Cause: " + e.getMessage());
				if (Settings.isDebugModeEnabled())
					e.printStackTrace();
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
		if (DIRECTORY.exists() && DIRECTORY.isDirectory())
			for (File f : DIRECTORY.listFiles()) {
				String name = f.getName();
				if (name.endsWith(".design"))
					names.add(name.replace(".design", ""));
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
		Action a = event.getAction();
		if (a == Action.LEFT_CLICK_BLOCK || a == Action.RIGHT_CLICK_BLOCK) {
			Player p = event.getPlayer();
			if (p.getItemInHand().isSimilar(wand)) {
				event.setCancelled(true);
				Location l = event.getClickedBlock().getLocation();
				boolean first = a == Action.LEFT_CLICK_BLOCK;
				selectPosition(p, l, first);
				if (first)
					p.sendMessage(plugin.messageManager.design_wand_first_position_selected(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName()));
				else
					p.sendMessage(plugin.messageManager.design_wand_second_position_selected(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName()));
			}
		}
	}
}