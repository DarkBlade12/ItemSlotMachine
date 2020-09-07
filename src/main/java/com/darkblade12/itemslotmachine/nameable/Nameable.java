package com.darkblade12.itemslotmachine.nameable;

import java.text.MessageFormat;
import java.util.List;

public interface Nameable {
    static String generateName(List<String> given, String pattern) {
        int index = pattern.indexOf("{0}");
        if (index == -1) {
            throw new IllegalArgumentException("Name pattern does not contain an id placeholder");
        }

        String prefix = pattern.substring(0, index).toLowerCase();
        String suffix = pattern.substring(index + 3).toLowerCase();
        int lastId = 0;
        for (String name : given) {
            String lowerName = name.toLowerCase();
            if (!lowerName.startsWith(prefix) || !lowerName.endsWith(suffix)) {
                continue;
            }

            int id;
            try {
                id = Integer.parseInt(lowerName.replace(prefix, "").replace(suffix, ""));
            } catch (NumberFormatException ex) {
                continue;
            }

            if (id > lastId) {
                lastId = id;
            }
        }
        return MessageFormat.format(pattern, ++lastId);
    }

    static String getIdString(String name, String pattern) {
        int index = pattern.indexOf("{0}");
        if (index == -1) {
            throw new IllegalArgumentException("The name pattern does not contain a number placeholder");
        }

        String prefix = pattern.substring(0, index).toLowerCase();
        String suffix = pattern.substring(index + 3).toLowerCase();
        String lowerName = name.toLowerCase();
        if (!lowerName.startsWith(prefix) || !lowerName.endsWith(suffix)) {
            return null;
        }
        return name.replace(prefix, "").replace(suffix, "");
    }

    static int tryGetId(String name, String pattern) {
        String id = getIdString(name, pattern);
        if (id == null) {
            return -1;
        }

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    String getName();
}