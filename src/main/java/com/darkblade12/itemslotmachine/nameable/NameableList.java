package com.darkblade12.itemslotmachine.nameable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class NameableList<T extends Nameable> extends ArrayList<T> {
    private static final long serialVersionUID = 2132464611329949798L;

    public NameableList() {
        super();
    }

    public NameableList(Collection<T> collection) {
        super(collection);
    }

    public T remove(String name, boolean caseInsensitive) {
        for (int i = 0; i < size(); i++) {
            String elemName = get(i).getName();
            if (caseInsensitive ? name.equalsIgnoreCase(elemName) : name.equals(elemName)) {
                return remove(i);
            }
        }
        return null;
    }

    public T remove(String name) {
        return remove(name, true);
    }

    public T get(String name, boolean caseInsensitive) {
        for (int i = 0; i < size(); i++) {
            T element = get(i);
            String elemName = element.getName();
            if (caseInsensitive ? name.equalsIgnoreCase(elemName) : name.equals(elemName)) {
                return element;
            }
        }
        return null;
    }

    public T get(String name) {
        return get(name, true);
    }

    public boolean containsName(String name, boolean caseInsensitive) {
        return get(name, caseInsensitive) != null;
    }

    public boolean containsName(String name) {
        return containsName(name, true);
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<String>(size());
        for (int i = 0; i < size(); i++) {
            names.add(get(i).getName());
        }
        return names;
    }

    public String toString(String separator, boolean useName) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (builder.length() > 0) {
                builder.append(separator);
            }
            T element = get(i);
            builder.append(useName ? element.getName() : element.toString());
        }
        return builder.toString();
    }

    public String toString(String separator) {
        return toString(separator, true);
    }
}