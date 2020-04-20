package com.darkblade12.itemslotmachine.statistic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.nameable.NameableList;
import com.darkblade12.itemslotmachine.util.FileUtils;

public abstract class Statistic implements Nameable {
    private NameableList<StatisticRecord> records;
    protected String name;

    public Statistic(String name) {
        this.name = name;
        this.records = new NameableList<StatisticRecord>();
    }

    public Statistic(String name, Collection<StatisticRecord> records) {
        this.name = name;
        this.records = new NameableList<StatisticRecord>(records);
    }

    public Statistic(String name, StatisticRecord... records) {
        this(name, Arrays.asList(records));
    }

    public Statistic(String name, Category... categories) {
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
    
    public void saveToFile() throws IOException {
        FileUtils.saveJson(getPath(), this);
    }

    public void deleteFile() {
        File file = new File(getPath());

        if (file.exists()) {
            file.delete();
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public abstract String getPath();

    public List<StatisticRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public StatisticRecord getRecord(String name) {
        return records.get(name);
    }

    public StatisticRecord getRecord(Category category) {
        return getRecord(category.name());
    }

    @Override
    public String toString() {
        return records.toString("#");
    }
}