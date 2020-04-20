package com.darkblade12.itemslotmachine.nameable;

import java.util.Set;

public interface NameGenerator {
    String generateName();

    Set<String> getNames();
}