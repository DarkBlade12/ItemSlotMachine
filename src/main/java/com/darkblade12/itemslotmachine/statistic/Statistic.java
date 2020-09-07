package com.darkblade12.itemslotmachine.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.util.FileUtils;

public abstract class Statistic implements Nameable {
    public static final String FILE_EXTENSION = ".json";
    protected NameableList<Record> records;
    protected String name;

    protected Statistic(String name) {
        this.name = name;
        this.records = new NameableList<Record>();
    }

    protected Statistic(String name, Collection<Record> records) {
        this.name = name;
        this.records = new NameableList<Record>(records);
    }

    protected Statistic(String name, Record... records) {
        this(name, Arrays.asList(records));
    }

    protected Statistic(String name, Category... categories) {
        this(name);

        for (Category type : categories) {
            records.add(type.createObject());
        }
    }

    public void reset() {
        for (int i = 0; i < records.size(); i++) {
            records.get(i).resetValue();
        }
    }

    public void saveFile(File directory) throws IOException {
        FileUtils.saveJson(new File(getSubDirectory(directory), getFileName()), this);
    }

    public void deleteFile(File directory) throws SecurityException {
        File file = new File(getSubDirectory(directory), getFileName());
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public String getFileName() {
        return name + FILE_EXTENSION;
    }

    public abstract String getSubDirectoryName();

    public File getSubDirectory(File directory) {
        return new File(directory, getSubDirectoryName());
    }

    public List<Record> getRecords() {
        return new ArrayList<Record>(records);
    }

    public Record getRecord(String name) {
        return records.get(name);
    }

    public Record getRecord(Category category) {
        return getRecord(category.name());
    }
}