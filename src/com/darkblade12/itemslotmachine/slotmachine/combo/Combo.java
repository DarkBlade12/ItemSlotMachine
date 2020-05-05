package com.darkblade12.itemslotmachine.slotmachine.combo;

import org.bukkit.Material;

public class Combo {
    private Material[] pattern;
    private Action[] actions;

    public Combo(Material[] pattern, Action[] actions) {
        if (pattern.length != 3) {
            throw new IllegalArgumentException("The length of symbols must be 3");
        }

        this.pattern = pattern;
        this.actions = actions;
    }

    public boolean isActivated(Material[] pattern) {
        for (int i = 0; i < pattern.length; i++) {
            if (this.pattern[i] != Material.AIR && pattern[i] != this.pattern[i]) {
                return false;
            }
        }
        return true;
    }

    public Material[] getPattern() {
        return pattern.clone();
    }

    public Action[] getActions() {
        return actions.clone();
    }
}