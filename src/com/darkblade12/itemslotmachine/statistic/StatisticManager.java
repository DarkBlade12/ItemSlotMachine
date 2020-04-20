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
    private static final String FILE_EXTENSION = ".json";
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
        for (String name : getNames()) {
            try {
                PlayerStatistic statistic = PlayerStatistic.fromFile(name);
                statistics.add(statistic);
            } catch (Exception e) {
                plugin.logWarning("Failed to load player statistic '" + name + "'!");
                if (Settings.isDebugModeEnabled()) {
                    e.printStackTrace();
                }
            }
        }

        int amount = statistics.size();
        plugin.logInfo(amount + " player statistic" + (amount == 1 ? "" : "s") + " loaded.");
    }

    public void register(PlayerStatistic statistic) {
        try {
            statistic.saveToFile();
            statistics.add(statistic);
        } catch (Exception e) {
            plugin.logWarning("Failed to save player statistic '" + statistic.getName() + "'!");
            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }
        }
    }

    public PlayerStatistic create(String name) {
        PlayerStatistic s = new PlayerStatistic(name);
        register(s);
        return s;
    }

    public PlayerStatistic create(Player player) {
        return create(player.getName());
    }

    public Set<String> getNames() {
        Set<String> names = new HashSet<String>();

        if (!PlayerStatistic.DIRECTORY.exists() || !PlayerStatistic.DIRECTORY.isDirectory()) {
            return names;
        }

        for (File file : PlayerStatistic.DIRECTORY.listFiles()) {
            String name = file.getName();
            if (name.endsWith(FILE_EXTENSION)) {
                names.add(name.replace(FILE_EXTENSION, ""));
            }
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

    public PlayerStatistic getStatistic(Player player, boolean create) {
        return getStatistic(player.getName(), create);
    }

    public PlayerStatistic getStatistic(String name) {
        return statistics.get(name);
    }

    public PlayerStatistic getStatistic(Player player) {
        return getStatistic(player.getName());
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

    public List<PlayerStatistic> getTop(Category category) {
        List<PlayerStatistic> top = new ArrayList<PlayerStatistic>(statistics);
        Collections.sort(top, new StatisticComparator(category));
        return top;
    }
}