package com.darkblade12.itemslotmachine.statistic;

import java.io.File;
import java.io.IOException;

import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class SlotMachineStatistic extends Statistic {
    public static final File DIRECTORY = new File("plugins/ItemSlotMachine/statistics/slot machine/");

    public SlotMachineStatistic(String name) {
        super(name, Category.TOTAL_SPINS, Category.WON_SPINS, Category.LOST_SPINS);
    }

    public static SlotMachineStatistic fromFile(File file) throws IOException, JsonIOException, JsonSyntaxException {
        return FileUtils.readJson(file, SlotMachineStatistic.class);
    }

    public static SlotMachineStatistic fromFile(String path) throws IOException, JsonIOException, JsonSyntaxException {
        return fromFile(new File(path));
    }

    @Override
    public String getSubDirectoryName() {
        return "slot machine";
    }
}