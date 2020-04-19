package com.darkblade12.itemslotmachine.item;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemFactory {
    private ItemFactory() {}

    public static ItemStack fromString(String s, boolean amount) throws IllegalArgumentException {
        String[] i = s.split("-");
        Material m = Material.matchMaterial(i[0]);
        if (m == null)
            throw new IllegalArgumentException("contains an invalid item name");
        return new ItemStack(m, amount ? i.length >= 2 ? Integer.parseInt(i[1]) : 1 : 1);
    }

    public static ItemStack fromString(String s) throws Exception {
        return fromString(s, true);
    }

    public static String toString(ItemStack i, boolean amount) {
        return i.getType().getKey().getKey() + (amount ? "-" + i.getAmount() : "");
    }

    public static String toString(ItemStack i) {
        return toString(i, true);
    }

    public static ItemStack setName(ItemStack i, String name) {
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack setLore(ItemStack i, List<String> lore) {
        ItemMeta meta = i.getItemMeta();
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack setLore(ItemStack i, String... lore) {
        return setLore(i, Arrays.asList(lore));
    }

    public static ItemStack setNameAndLore(ItemStack i, String name, List<String> lore) {
        return setLore(setName(i, name), lore);
    }

    public static ItemStack setNameAndLore(ItemStack i, String name, String... lore) {
        return setNameAndLore(i, name, Arrays.asList(lore));
    }
}