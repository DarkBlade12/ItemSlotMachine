package com.darkblade12.itemslotmachine.statistic;

import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class SlotMachineStatistic extends Statistic implements Nameable {
    private final String name;

    public SlotMachineStatistic(String name) {
        super(Arrays.stream(Category.values()).filter(Category::isSlotMachineCategory).collect(Collectors.toList()));
        this.name = name;
    }

    public static SlotMachineStatistic fromFile(File file) throws IOException, JsonParseException {
        return FileUtils.readJson(file, SlotMachineStatistic.class);
    }

    public static SlotMachineStatistic fromFile(String path) throws IOException, JsonParseException {
        return fromFile(new File(path));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFileName() {
        return name + FILE_EXTENSION;
    }

    @Override
    public String getSubDirectoryName() {
        return "slot machine";
    }
}
