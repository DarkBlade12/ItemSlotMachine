package com.darkblade12.itemslotmachine.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Manager;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class StatisticManager extends Manager<ItemSlotMachine> {
    private final NameableList<SlotMachineStatistic> slotStats;
    private final NameableList<PlayerStatistic> playerStats;
    private final File slotDirectory;
    private final File playerDirectory;
    private NameableComparator<SlotMachineStatistic> defaultComparator;

    public StatisticManager(ItemSlotMachine plugin) {
        super(plugin, new File(plugin.getDataFolder(), "statistics"));
        slotStats = new NameableList<SlotMachineStatistic>();
        playerStats = new NameableList<PlayerStatistic>();
        slotDirectory = new File(dataDirectory, "slot machine");
        playerDirectory = new File(dataDirectory, "player");
    }

    @Override
    public void onEnable() {
        defaultComparator = new NameableComparator<SlotMachineStatistic>(Settings.getSlotMachineNamePattern());
        loadStatistics();
    }

    @Override
    public void onDisable() {
        playerStats.clear();
    }

    public void loadStatistics() {
        for (File file : FileUtils.getFiles(slotDirectory, Statistic.FILE_EXTENSION)) {
            try {
                SlotMachineStatistic stat = SlotMachineStatistic.fromFile(file);
                slotStats.add(stat);
            } catch (IOException | JsonIOException | JsonSyntaxException ex) {
                plugin.logException("Failed to load slot machine statistic file '" + file.getName() + "': %c", ex);
            }
        }

        for (File file : FileUtils.getFiles(playerDirectory, Statistic.FILE_EXTENSION)) {
            try {
                PlayerStatistic stat = PlayerStatistic.fromFile(file);
                playerStats.add(stat);
            } catch (IOException | JsonIOException | JsonSyntaxException ex) {
                plugin.logException("Failed to load player statistic file '" + file.getName() + "': %c", ex);
            }
        }

        int slotCount = slotStats.size();
        int playerCount = playerStats.size();
        plugin.logInfo("Statistics successfully loaded. (slot machine: " + slotCount + ", player: " + playerCount + ")");
    }

    public boolean trySave(Statistic statistic) {
        try {
            statistic.saveFile(dataDirectory);
            return true;
        } catch (IOException ex) {
            String type = statistic instanceof SlotMachineStatistic ? "slot machine" : "player";
            plugin.logException("Failed to save " + type + " statistic file '" + statistic.getFileName() + "': %c", ex);
            return false;
        }
    }

    public <T extends Statistic> boolean register(Statistic statistic) {
        if (!trySave(statistic)) {
            return false;
        }

        if (statistic instanceof SlotMachineStatistic) {
            slotStats.add((SlotMachineStatistic) statistic);
            return true;
        } else if (statistic instanceof PlayerStatistic) {
            playerStats.add((PlayerStatistic) statistic);
            return true;
        }
        return false;
    }

    public SlotMachineStatistic createSlotMachineStatistic(String name) {
        SlotMachineStatistic stat = new SlotMachineStatistic(name);
        return register(stat) ? stat : null;
    }

    public SlotMachineStatistic createSlotMachineStatistic(SlotMachine slot) {
        return createSlotMachineStatistic(slot.getName());
    }

    public void deleteSlotMachineStatistic(String name) {
        SlotMachineStatistic stat = getSlotMachineStatistic(name);
        if (stat != null) {
            stat.deleteFile(slotDirectory);
        }
    }

    public void deleteSlotMachineStatistic(SlotMachine slot) {
        deleteSlotMachineStatistic(slot.getName());
    }

    public List<String> getSlotMachineNames() {
        return getSlotMachineStatistics().getNames();
    }

    public NameableList<SlotMachineStatistic> getSlotMachineStatistics() {
        NameableList<SlotMachineStatistic> clone = new NameableList<>(slotStats);
        clone.sort(defaultComparator);
        return clone;
    }

    public SlotMachineStatistic getSlotMachineStatistic(String name, boolean create) {
        SlotMachineStatistic stat = slotStats.get(name);
        if (stat != null) {
            return stat;
        } else if (!create) {
            return null;
        }
        return createSlotMachineStatistic(name);
    }

    public SlotMachineStatistic getSlotMachineStatistic(String name) {
        return getSlotMachineStatistic(name, false);
    }

    public SlotMachineStatistic getSlotMachineStatistic(SlotMachine slot, boolean create) {
        return getSlotMachineStatistic(slot.getName(), create);
    }

    public SlotMachineStatistic getSlotMachineStatistic(SlotMachine slot) {
        return getSlotMachineStatistic(slot, false);
    }

    public boolean hasSlotMachineStatistic(String name) {
        return slotStats.containsName(name);
    }

    public boolean hasSlotMachineStatistic(SlotMachine slot) {
        return hasSlotMachineStatistic(slot.getName());
    }

    public int getSlotMachineStatisticCount() {
        return slotStats.size();
    }

    public List<SlotMachineStatistic> getSlotMachineTop(Category category) {
        List<SlotMachineStatistic> top = new ArrayList<>(slotStats);
        top.sort(new StatisticComparator(category));
        return top;
    }

    public PlayerStatistic createPlayerStatistic(UUID id) {
        PlayerStatistic stat = new PlayerStatistic(id);
        return register(stat) ? stat : null;
    }

    public PlayerStatistic createPlayerStatistic(Player player) {
        return createPlayerStatistic(player.getUniqueId());
    }

    public PlayerStatistic createPlayerStatistic(OfflinePlayer player) {
        return createPlayerStatistic(player.getUniqueId());
    }

    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < playerStats.size(); i++) {
            names.add(playerStats.get(i).getPlayerName());
        }
        Collections.sort(names);
        return names;
    }

    public NameableList<PlayerStatistic> getPlayerStatistics() {
        NameableList<PlayerStatistic> clone = new NameableList<>(playerStats);
        clone.sort(new NameableComparator<PlayerStatistic>());
        return clone;
    }

    public PlayerStatistic getPlayerStatistic(UUID id, boolean create) {
        PlayerStatistic stat = playerStats.get(id.toString());
        if (stat != null) {
            return stat;
        } else if (!create) {
            return null;
        }
        return createPlayerStatistic(id);
    }

    public PlayerStatistic getPlayerStatistic(UUID id) {
        return getPlayerStatistic(id, false);
    }

    public PlayerStatistic getPlayerStatistic(Player player, boolean create) {
        return getPlayerStatistic(player.getUniqueId(), create);
    }

    public PlayerStatistic getPlayerStatistic(Player player) {
        return getPlayerStatistic(player.getUniqueId());
    }

    public PlayerStatistic getPlayerStatistic(OfflinePlayer player, boolean create) {
        return getPlayerStatistic(player.getUniqueId(), create);
    }

    public PlayerStatistic getPlayerStatistic(OfflinePlayer player) {
        return getPlayerStatistic(player.getUniqueId());
    }

    public PlayerStatistic getPlayerStatistic(String name) {
        for (int i = 0; i < playerStats.size(); i++) {
            PlayerStatistic stat = playerStats.get(i);
            if (stat.getPlayer().getName().equalsIgnoreCase(name)) {
                return stat;
            }
        }
        return null;
    }

    public boolean hasPlayerStatistic(UUID id) {
        return playerStats.containsName(id.toString());
    }

    public boolean hasPlayerStatistic(Player player) {
        return hasPlayerStatistic(player.getUniqueId());
    }

    public boolean hasPlayerStatistic(OfflinePlayer player) {
        return hasPlayerStatistic(player.getUniqueId());
    }

    public boolean hasPlayerStatistic(String name) {
        return getPlayerStatistic(name) != null;
    }

    public int getPlayerStatisticCount() {
        return playerStats.size();
    }

    public List<PlayerStatistic> getPlayerTop(Category category) {
        List<PlayerStatistic> top = new ArrayList<>(playerStats);
        top.sort(new StatisticComparator(category));
        return top;
    }
}