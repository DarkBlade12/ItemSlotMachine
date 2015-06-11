package com.darkblade12.itemslotmachine.slotmachine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
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
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.manager.Manager;
import com.darkblade12.itemslotmachine.nameable.NameGenerator;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.statistic.StatisticComparator;
import com.darkblade12.itemslotmachine.statistic.Type;
import com.darkblade12.itemslotmachine.statistic.types.SlotMachineStatistic;

public final class SlotMachineManager extends Manager implements NameGenerator {
	private static final File DIRECTORY = new File("plugins/ItemSlotMachine/slot machines/");
	private NameableComparator<SlotMachine> comparator;
	private NameableList<SlotMachine> slotMachines;

	public SlotMachineManager(ItemSlotMachine plugin) {
		super(plugin);
		onInitialize();
	}

	@Override
	public boolean onInitialize() {
		comparator = new NameableComparator<SlotMachine>(Settings.getRawSlotMachineName());
		loadSlotMachines();
		registerEvents();
		return true;
	}

	@Override
	public void onDisable() {
		unregisterAll();
		for (int i = 0; i < slotMachines.size(); i++)
			slotMachines.get(i).deactivate();
	}

	@Override
	public String generateName() {
		Set<Integer> used = new HashSet<Integer>();
		for (String name : getNames())
			if (name.contains(Settings.getRawSlotMachineName()))
				try {
					used.add(Integer.parseInt(name.replace(Settings.getRawSlotMachineName(), "")));
				} catch (Exception e) {
					/* custom ids are ignored */
				}
		int n = 1;
		while (used.contains(n))
			n++;
		return Settings.getDefaultSlotMachineName().replace("<num>", Integer.toString(n));
	}

	private void sort() {
		Collections.sort(slotMachines, comparator);
	}

	public void loadSlotMachines() {
		slotMachines = new NameableList<SlotMachine>(true);
		for (String name : getNames())
			try {
				slotMachines.add(SlotMachine.load(plugin, name));
			} catch (Exception e) {
				plugin.l.warning("Failed to load slot machine '" + name + "'! Cause: " + e.getMessage());
				if (Settings.isDebugModeEnabled())
					e.printStackTrace();
			}
		sort();
		int amount = slotMachines.size();
		plugin.l.info(amount + " slot machine" + (amount == 1 ? "" : "s") + " loaded.");
	}

	public void register(SlotMachine s) {
		slotMachines.add(s);
		sort();
	}

	public void unregister(SlotMachine s) {
		slotMachines.remove(s.getName());
		sort();
		s.destruct();
	}

	public void reload(SlotMachine s) throws Exception {
		s.deactivate();
		slotMachines.remove(s.name);
		slotMachines.add(SlotMachine.load(plugin, s.name));
	}

	private void deactivateUsed(Player p) {
		for (int i = 0; i < slotMachines.size(); i++) {
			SlotMachine s = slotMachines.get(i);
			if (s.isUser(p))
				s.deactivate();
		}
	}

	@Override
	public Set<String> getNames() {
		Set<String> names = new HashSet<String>();
		if (DIRECTORY.exists() && DIRECTORY.isDirectory())
			for (File f : DIRECTORY.listFiles()) {
				String name = f.getName();
				if (name.endsWith(".yml"))
					names.add(name.replace(".yml", ""));
			}
		return names;
	}

	public boolean hasName(String name) {
		for (String n : getNames())
			if (n.equalsIgnoreCase(name))
				return true;
		return false;
	}

	public List<SlotMachine> getSlotMachines() {
		return Collections.unmodifiableList(slotMachines);
	}

	public SlotMachine getSlotMachine(String name) {
		return slotMachines.get(name);
	}

	public SlotMachine getSlotMachine(Location l) {
		for (int i = 0; i < slotMachines.size(); i++) {
			SlotMachine s = slotMachines.get(i);
			if (s.isInsideRegion(l))
				return s;
		}
		return null;
	}

	private SlotMachine getInteractedSlotMachine(Location l) {
		for (int i = 0; i < slotMachines.size(); i++) {
			SlotMachine s = slotMachines.get(i);
			if (s.hasInteracted(l))
				return s;
		}
		return null;
	}

	public boolean hasSlotMachine(String name) {
		return slotMachines.contains(name);
	}

	public int getSlotMachineAmount() {
		return slotMachines.size();
	}

	private int getActivatedAmount(Player p) {
		int a = 0;
		for (int i = 0; i < slotMachines.size(); i++) {
			SlotMachine s = slotMachines.get(i);
			if (s.isUser(p) && s.isActive())
				a++;
		}
		return a;
	}

	public List<SlotMachineStatistic> getTop(Type t) {
		List<SlotMachineStatistic> top = new ArrayList<SlotMachineStatistic>();
		for (int i = 0; i < slotMachines.size(); i++)
			top.add(slotMachines.get(i).getStatistic());
		Collections.sort(top, new StatisticComparator(t));
		return top;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingPlace(HangingPlaceEvent event) {
		Player p = event.getPlayer();
		SlotMachine s = getSlotMachine(event.getBlock().getLocation());
		if (s != null && !s.isPermittedToModify(p)) {
			event.setCancelled(true);
			p.sendMessage(plugin.messageManager.slot_machine_modifying_not_allowed());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreak(HangingBreakEvent event) {
		SlotMachine s = getSlotMachine(event.getEntity().getLocation());
		if (s != null)
			if (event instanceof HangingBreakByEntityEvent) {
				Entity e = ((HangingBreakByEntityEvent) event).getRemover();
				if (e instanceof Player) {
					Player p = (Player) e;
					if (!s.isPermittedToModify(p)) {
						event.setCancelled(true);
						p.sendMessage(plugin.messageManager.slot_machine_modifying_not_allowed());
					}
				} else
					event.setCancelled(true);
			} else
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		SlotMachine s = getSlotMachine(event.getBlock().getLocation());
		if (s != null && !s.isPermittedToModify(p)) {
			event.setCancelled(true);
			p.sendMessage(plugin.messageManager.slot_machine_modifying_not_allowed());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		SlotMachine s = getSlotMachine(event.getBlock().getLocation());
		if (s == null)
			s = getSlotMachine(event.getBlockAgainst().getLocation());
		if (s != null && !s.isPermittedToModify(p)) {
			event.setCancelled(true);
			p.sendMessage(plugin.messageManager.slot_machine_modifying_not_allowed());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Entity e = event.getRightClicked();
		if (e instanceof Hanging) {
			SlotMachine s = getSlotMachine(e.getLocation());
			if (s != null && !s.isPermittedToModify(p)) {
				event.setCancelled(true);
				p.sendMessage(plugin.messageManager.slot_machine_modifying_not_allowed());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Hanging) {
			SlotMachine s = getSlotMachine(e.getLocation());
			if (s != null)
				if (event instanceof EntityDamageByEntityEvent) {
					Entity d = ((EntityDamageByEntityEvent) event).getDamager();
					if (d instanceof Player) {
						Player p = (Player) d;
						if (!s.isPermittedToModify(p)) {
							event.setCancelled(true);
							p.sendMessage(plugin.messageManager.slot_machine_modifying_not_allowed());
						}
					} else
						event.setCancelled(true);
				} else
					event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack h = p.getItemInHand();
			Location l = event.getClickedBlock().getLocation();
			SlotMachine s = getSlotMachine(l);
			if (h.getType() == Material.WATER_BUCKET || h.getType() == Material.LAVA_BUCKET) {
				if (s != null && !s.isPermittedToModify(p)) {
					event.setCancelled(true);
					p.sendMessage(plugin.messageManager.slot_machine_modifying_not_allowed());
					return;
				}
				return;
			}
			if (s != null) {
				if (!plugin.coinManager.isCoin(h)) {
					if (!h.getType().isBlock() || h.getType() == Material.AIR)
						if (p.hasPermission("ItemSlotMachine.slot.check") || p.hasPermission("ItemSlotMachine.slot.*") || p.hasPermission("ItemSlotMachine.*"))
							p.sendMessage(plugin.messageManager.slot_machine_clicked(s.getName()));
				} else if (s.hasInteracted(l)) {
					event.setCancelled(true);
					if (!s.isPermittedToUse(p)) {
						p.sendMessage(plugin.messageManager.slot_machine_usage_not_allowed());
					} else {
						if (s.isBroken())
							p.sendMessage(plugin.messageManager.slot_machine_broken());
						else if (s.isActive())
							p.sendMessage(plugin.messageManager.slot_machine_still_active());
						else if (s.isPlayerLockEnabled() && !s.isLockExpired() && !s.isUser(p))
							p.sendMessage(plugin.messageManager.slot_machine_locked(s.getUserName(), s.getRemainingLockTime()));
						else if (p.getGameMode() == GameMode.CREATIVE && !s.isCreativeUsageEnabled())
							p.sendMessage(plugin.messageManager.slot_machine_creative_not_allowed());
						else if (!s.hasEnoughCoins(p))
							p.sendMessage(plugin.messageManager.slot_machine_not_enough_coins(s.getActivationAmount()));
						else if (Settings.isLimitedUsageEnabled() && getActivatedAmount(p) + 1 > Settings.getLimitedUsageAmount())
							p.sendMessage(plugin.messageManager.slot_machine_limited_usage());
						else
							s.activate(p);
					}
				}
			}
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			SlotMachine s = getInteractedSlotMachine(event.getClickedBlock().getLocation());
			if (s != null && s.isPermittedToHalt(p)) {
				event.setCancelled(true);
				s.halt();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		deactivateUsed(event.getPlayer());
	}
}