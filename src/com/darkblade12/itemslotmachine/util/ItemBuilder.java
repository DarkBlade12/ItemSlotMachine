package com.darkblade12.itemslotmachine.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemBuilder {
    private Material material;
    private int amount = 1;
    private String name;
    private List<String> lore;
    private boolean unbreakable;

    public ItemBuilder() {}

    public static ItemStack fromString(String s, boolean amount) throws IllegalArgumentException {
        String[] data = s.split("-");
        Material material = Material.matchMaterial(data[0]);
        if (material == null) {
            throw new IllegalArgumentException("contains an invalid item name");
        }

        return new ItemStack(material, amount ? data.length >= 2 ? Integer.parseInt(data[1]) : 1 : 1);
    }

    public static ItemStack fromString(String s) throws IllegalArgumentException {
        return fromString(s, true);
    }

    public static String toString(ItemStack item, boolean amount) {
        StringBuilder builder = new StringBuilder(item.getType().getKey().getKey());
        if (amount) {
            builder.append("-" + item.getAmount());
        }

        return builder.toString();
    }

    public static String toString(ItemStack i) {
        return toString(i, true);
    }

    public ItemBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder withLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder withLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder withUnbreakable() {
        return unbreakable(true);
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
        }

        if (lore != null && lore.size() > 0) {
            meta.setLore(lore);
        }

        meta.setUnbreakable(unbreakable);
        item.setItemMeta(meta);
        return item;
    }
}
