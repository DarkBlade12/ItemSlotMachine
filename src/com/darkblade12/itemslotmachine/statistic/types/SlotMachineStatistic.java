package com.darkblade12.itemslotmachine.statistic.types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.darkblade12.itemslotmachine.statistic.Category;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.util.FileUtils;

public final class SlotMachineStatistic extends Statistic {
    public static final File DIRECTORY = new File("plugins/ItemSlotMachine/statistics/slot machine/");

    public SlotMachineStatistic(String name) {
        super(name, Category.TOTAL_SPINS, Category.WON_SPINS, Category.LOST_SPINS);
    }

    public static String getPath(String name) {
        return DIRECTORY.getPath() + "/" + name + ".json";
    }

    public static SlotMachineStatistic fromFile(String name) throws FileNotFoundException, IOException {
        return FileUtils.readJson(getPath(name), SlotMachineStatistic.class);
    }

    @Override
    public String getPath() {
        return getPath(name);
    }
}