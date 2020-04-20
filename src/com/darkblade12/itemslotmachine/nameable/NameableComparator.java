package com.darkblade12.itemslotmachine.nameable;

import java.util.Comparator;

public final class NameableComparator<T extends Nameable> implements Comparator<T> {
    private String rawName;

    public NameableComparator(String rawName) {
        this.rawName = rawName;
    }

    @Override
    public int compare(T e1, T e2) {
        String name1 = e1.getName();
        String name2 = e2.getName();
        try {
            int x1 = Integer.parseInt(name1.replace(rawName, ""));
            int x2 = Integer.parseInt(name2.replace(rawName, ""));
            return x1 - x2;
        } catch (Exception e) {
            return name1.compareTo(name2);
        }
    }
}