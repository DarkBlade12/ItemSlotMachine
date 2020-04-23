package com.darkblade12.itemslotmachine.statistic;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class PlayerStatistic extends Statistic {
    public PlayerStatistic(UUID id) {
        super(id.toString(), Category.values());
    }

    public static PlayerStatistic fromFile(File file) throws IOException, JsonIOException, JsonSyntaxException {
        return FileUtils.readJson(file, PlayerStatistic.class);
    }

    public static PlayerStatistic fromFile(String path) throws IOException, JsonIOException, JsonSyntaxException {
        return fromFile(new File(path));
    }

    @Override
    public String getSubDirectoryName() {
        return "player";
    }

    public UUID getId() {
        return UUID.fromString(name);
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(getId());
    }

    public String getPlayerName() {
        return getPlayer().getName();
    }
}