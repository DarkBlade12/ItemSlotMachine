package com.darkblade12.itemslotmachine.nameable;

import java.util.Comparator;

public class NameableComparator<T extends Nameable> implements Comparator<T> {
    private final String namePattern;

    public NameableComparator(String namePattern) {
        if(namePattern != null && !namePattern.contains("{0}")) {
            throw new IllegalArgumentException("Name pattern does not contain an id placeholder");
        }
        this.namePattern = namePattern;
    }

    public NameableComparator() {
        this(null);
    }

    @Override
    public int compare(T e1, T e2) {
        String name1 = e1.getName();
        String name2 = e2.getName();
        if (namePattern == null) {
            return name1.compareTo(name2);
        }

        int id1 = Nameable.tryGetId(name1, namePattern);
        int id2 = Nameable.tryGetId(name1, namePattern);
        return id1 == -1 || id2 == -1 ? name1.compareTo(name2) : id1 - id2;
    }
}