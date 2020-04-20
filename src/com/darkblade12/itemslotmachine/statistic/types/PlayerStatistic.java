package com.darkblade12.itemslotmachine.statistic.types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.darkblade12.itemslotmachine.statistic.Category;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.util.FileUtils;

public final class PlayerStatistic extends Statistic {
    public static final File DIRECTORY = new File("plugins/ItemSlotMachine/statistics/player/");

    public PlayerStatistic(String name) {
        super(name, Category.values());
    }

    public static String getPath(String name) {
        return DIRECTORY.getPath() + "/" + name + ".json";
    }

    public static PlayerStatistic fromFile(String name) throws FileNotFoundException, IOException {
        return FileUtils.readJson(getPath(name), PlayerStatistic.class);
    }

    @Override
    public String getPath() {
        return getPath(name);
    }
}