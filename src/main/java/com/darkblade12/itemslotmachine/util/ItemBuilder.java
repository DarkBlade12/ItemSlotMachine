package com.darkblade12.itemslotmachine.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class ItemBuilder {
    private Material material;
    private int amount = 1;
    private String name;
    private List<String> lore;
    private boolean unbreakable;

    public ItemBuilder withType(Material material) {
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
        return withLore(Arrays.asList(lore));
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
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }

            if (lore != null && lore.size() > 0) {
                meta.setLore(lore);
            }

            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }

        return item;
    }
}
