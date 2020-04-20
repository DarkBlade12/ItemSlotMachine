package com.darkblade12.itemslotmachine.nameable;

import java.util.Comparator;

public final class NameableComparator<T extends Nameable> implements Comparator<T> {
    @Override
    public int compare(T e1, T e2) {
        return e1.getName().compareTo(e2.getName());
    }
}