package com.darkblade12.itemslotmachine.statistic;

import com.darkblade12.itemslotmachine.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public abstract class Statistic implements Iterable<Record> {
    public static final String FILE_EXTENSION = ".json";
    protected ConcurrentLinkedQueue<Record> records;

    protected Statistic(Collection<Category> categories) {
        this.records = categories.stream().map(Category::createRecord).collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
    }

    protected Statistic(Category... categories) {
        this(Arrays.asList(categories));
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }

    public void reset() {
        records.forEach(Record::resetValue);
    }

    public void saveFile(File directory) throws IOException {
        FileUtils.saveJson(new File(getSubDirectory(directory), getFileName()), this);
    }

    public boolean deleteFile(File directory) {
        File file = new File(getSubDirectory(directory), getFileName());
        try {
            return file.delete();
        } catch (SecurityException e) {
            return false;
        }
    }

    public abstract String getFileName();

    public abstract String getSubDirectoryName();

    public File getSubDirectory(File directory) {
        return new File(directory, getSubDirectoryName());
    }

    public Record getRecord(Category category) {
        return records.stream().filter(r -> r.getCategory() == category).findFirst().orElse(null);
    }
}
