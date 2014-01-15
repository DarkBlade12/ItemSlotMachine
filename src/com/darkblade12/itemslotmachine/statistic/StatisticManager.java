package com.darkblade12.itemslotmachine.statistic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.manager.Manager;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.statistic.types.PlayerStatistic;

public final class StatisticManager extends Manager {
	private static final File DIRECTORY = new File("plugins/ItemSlotMachine/statistics/player/");
	private NameableList<PlayerStatistic> statistics;

	public StatisticManager(ItemSlotMachine plugin) {
		super(plugin);
		onInitialize();
	}

	@Override
	public boolean onInitialize() {
		loadStatistics();
		return true;
	}

	@Override
	public void onDisable() {}

	public void loadStatistics() {
		statistics = new NameableList<PlayerStatistic>(true);
		for (String name : getNames())
			try {
				statistics.add(PlayerStatistic.fromFile(name));
			} catch (Exception e) {
				plugin.l.warning("Failed to load player statistic '" + name + "'! Cause: " + e.getMessage());
				if (Settings.isDebugModeEnabled())
					e.printStackTrace();
			}
		int amount = statistics.size();
		plugin.l.info(amount + " player statistic" + (amount == 1 ? "" : "s") + " loaded.");
	}

	public void register(PlayerStatistic p) {
		statistics.add(p);
		p.saveToFile();
	}

	public PlayerStatistic create(String name) {
		PlayerStatistic s = new PlayerStatistic(name);
		register(s);
		return s;
	}

	public PlayerStatistic create(Player p) {
		return create(p.getName());
	}

	public Set<String> getNames() {
		Set<String> names = new HashSet<String>();
		if (DIRECTORY.exists() && DIRECTORY.isDirectory())
			for (File f : DIRECTORY.listFiles()) {
				String name = f.getName();
				if (name.endsWith(".statistic"))
					names.add(name.replace(".statistic", ""));
			}
		return names;
	}

	public List<PlayerStatistic> getStatistics() {
		return Collections.unmodifiableList(statistics);
	}

	public PlayerStatistic getStatistic(String name, boolean create) {
		PlayerStatistic p = getStatistic(name);
		return p == null ? create ? create(name) : null : p;
	}

	public PlayerStatistic getStatistic(Player p, boolean create) {
		return getStatistic(p.getName(), create);
	}

	public PlayerStatistic getStatistic(String name) {
		return statistics.get(name);
	}

	public PlayerStatistic getStatistic(Player p) {
		return getStatistic(p.getName());
	}

	public boolean hasStatistic(String name) {
		return statistics.contains(name);
	}

	public boolean hasStatistic(Player p) {
		return hasStatistic(p.getName());
	}

	public int getStatisticAmount() {
		return statistics.size();
	}

	public List<PlayerStatistic> getTop(Type t) {
		List<PlayerStatistic> top = new ArrayList<PlayerStatistic>(statistics);
		Collections.sort(top, new StatisticComparator(t));
		return top;
	}
}