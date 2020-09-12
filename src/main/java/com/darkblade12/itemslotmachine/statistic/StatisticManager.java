package com.darkblade12.itemslotmachine.statistic;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.Manager;
import com.darkblade12.itemslotmachine.nameable.NameableComparator;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public final class StatisticManager extends Manager<ItemSlotMachine> {
    private final ConcurrentLinkedQueue<SlotMachineStatistic> slotStats;
    private final ConcurrentLinkedQueue<PlayerStatistic> playerStats;
    private final File slotDirectory;
    private final File playerDirectory;
    private NameableComparator<SlotMachineStatistic> comparator;

    public StatisticManager(ItemSlotMachine plugin) {
        super(plugin, new File(plugin.getDataFolder(), "statistics"));
        slotStats = new ConcurrentLinkedQueue<>();
        playerStats = new ConcurrentLinkedQueue<>();
        slotDirectory = new File(dataDirectory, "slot machine");
        playerDirectory = new File(dataDirectory, "player");
    }

    @Override
    protected void onEnable() {
        comparator = new NameableComparator<>(plugin.getSettings().getSlotMachineNamePattern());
        loadStatistics();
    }

    @Override
    protected void onDisable() {
        playerStats.clear();
    }

    private void loadStatistics() {
        for (File file : FileUtils.getFiles(slotDirectory, Statistic.FILE_EXTENSION)) {
            try {
                SlotMachineStatistic stat = SlotMachineStatistic.fromFile(file);
                slotStats.add(stat);
            } catch (IOException | JsonParseException ex) {
                plugin.logException("Failed to load slot machine statistic file {1}: {0}", ex, file.getName());
            }
        }

        for (File file : FileUtils.getFiles(playerDirectory, Statistic.FILE_EXTENSION)) {
            try {
                PlayerStatistic stat = PlayerStatistic.fromFile(file);
                if (stat.getId() == null) {
                    convertPlayerStatistic(file);
                    stat = PlayerStatistic.fromFile(file);
                }

                playerStats.add(stat);
            } catch (IOException | JsonParseException ex) {
                plugin.logException("Failed to load player statistic file {1}: {0}", ex, file.getName());
            }
        }

        plugin.logInfo("Statistics successfully loaded.");
    }

    private void convertPlayerStatistic(File file) throws IOException {
        JsonObject obj = FileUtils.readJson(file, JsonObject.class);
        String id = obj.get("name").getAsString();
        obj.remove("name");
        obj.addProperty("id", id);
        FileUtils.saveJson(file, obj);
    }

    public boolean trySave(Statistic statistic) {
        try {
            statistic.saveFile(dataDirectory);
            return true;
        } catch (IOException ex) {
            String type = statistic instanceof SlotMachineStatistic ? "slot machine" : "player";
            plugin.logException("Failed to save {1} statistic file {2}: {0}", ex, type, statistic.getFileName());
            return false;
        }
    }

    public boolean register(Statistic statistic) {
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
        return slotStats.stream().sorted(comparator).map(SlotMachineStatistic::getName).collect(Collectors.toList());
    }

    public SlotMachineStatistic getSlotMachineStatistic(String name) {
        return getSlotMachineStatistic(name, false);
    }

    public SlotMachineStatistic getSlotMachineStatistic(String name, boolean create) {
        return slotStats.stream().filter(s -> s.getName().equals(name)).findFirst()
                        .orElseGet(() -> create ? createSlotMachineStatistic(name) : null);
    }

    public SlotMachineStatistic getSlotMachineStatistic(SlotMachine slot, boolean create) {
        return getSlotMachineStatistic(slot.getName(), create);
    }

    public SlotMachineStatistic getSlotMachineStatistic(SlotMachine slot) {
        return getSlotMachineStatistic(slot, false);
    }

    public int getSlotMachineStatisticCount() {
        return slotStats.size();
    }

    public List<SlotMachineStatistic> getSlotMachineTop(Category category) {
        return slotStats.stream().sorted(new StatisticComparator(category)).collect(Collectors.toList());
    }

    public PlayerStatistic createPlayerStatistic(UUID id) {
        PlayerStatistic stat = new PlayerStatistic(id);
        return register(stat) ? stat : null;
    }

    public List<String> getPlayerNames() {
        return playerStats.stream().map(PlayerStatistic::getPlayerName).sorted().collect(Collectors.toList());
    }

    public PlayerStatistic getPlayerStatistic(UUID id) {
        return getPlayerStatistic(id, false);
    }

    public PlayerStatistic getPlayerStatistic(UUID id, boolean create) {
        return playerStats.stream().filter(s -> s.getId().equals(id)).findFirst()
                          .orElseGet(() -> create ? createPlayerStatistic(id) : null);
    }

    public PlayerStatistic getPlayerStatistic(Player player, boolean create) {
        return getPlayerStatistic(player.getUniqueId(), create);
    }

    public PlayerStatistic getPlayerStatistic(OfflinePlayer player) {
        return getPlayerStatistic(player.getUniqueId());
    }

    public PlayerStatistic getPlayerStatistic(String name) {
        return playerStats.stream().filter(s -> s.getPlayerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public int getPlayerStatisticCount() {
        return playerStats.size();
    }

    public List<PlayerStatistic> getPlayerTop(Category category) {
        return playerStats.stream().sorted(new StatisticComparator(category)).collect(Collectors.toList());
    }
}
