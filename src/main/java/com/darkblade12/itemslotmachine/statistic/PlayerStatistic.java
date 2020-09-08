package com.darkblade12.itemslotmachine.statistic;

import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonParseException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public final class PlayerStatistic extends Statistic {
    private final UUID id;

    public PlayerStatistic(UUID id) {
        super(Category.values());
        this.id = id;
    }

    public static PlayerStatistic fromFile(File file) throws IOException, JsonParseException {
        return FileUtils.readJson(file, PlayerStatistic.class);
    }

    public static PlayerStatistic fromFile(String path) throws IOException, JsonParseException {
        return fromFile(new File(path));
    }

    @Override
    public String getFileName() {
        return id  + FILE_EXTENSION;
    }

    @Override
    public String getSubDirectoryName() {
        return "player";
    }

    public UUID getId() {
        return id;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(getId());
    }

    public String getPlayerName() {
        return Optional.ofNullable(getPlayer().getName()).orElse("");
    }
}
