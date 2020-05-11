package com.darkblade12.itemslotmachine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class ItemUtils {
    private ItemUtils() {}

    public static ItemStack fromString(String text, boolean withAmount) throws IllegalArgumentException {
        String[] data = text.split("-");
        Material material = Material.matchMaterial(data[0]);
        if (material == null) {
            throw new IllegalArgumentException("contains an invalid item name");
        }

        return new ItemStack(material, withAmount ? data.length >= 2 ? Integer.parseInt(data[1]) : 1 : 1);
    }

    public static ItemStack fromString(String text) throws IllegalArgumentException {
        return fromString(text, true);
    }

    public static String toString(ItemStack item, boolean withAmount) {
        StringBuilder builder = new StringBuilder(item.getType().getKey().getKey());
        if (withAmount) {
            builder.append("-" + item.getAmount());
        }

        return builder.toString();
    }

    public static String toString(ItemStack item) {
        return toString(item, true);
    }

    public static List<ItemStack> listFromString(String text, boolean withAmount) throws IllegalArgumentException {
        String[] data = text.replace(" ", "").split(",");
        List<ItemStack> items = new ArrayList<>(data.length);
        for (String item : data) {
            items.add(fromString(item, withAmount));
        }
        return items;
    }

    public static List<ItemStack> listFromString(String text) throws IllegalArgumentException {
        return listFromString(text, true);
    }

    public static boolean hasEnoughSpace(Player player, ItemStack item) {
        int maxStack = item.getMaxStackSize();
        int space = 0;
        for (ItemStack invItem : player.getInventory().getStorageContents()) {
            if (invItem == null) {
                space += maxStack;
            } else if (invItem.isSimilar(item)) {
                space += maxStack - invItem.getAmount();
            }
        }
        return space >= item.getAmount();
    }

    public static int getTotalAmount(Player player, ItemStack item) {
        int total = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.isSimilar(item)) {
                total += invItem.getAmount();
            }
        }
        return total;
    }

    public static void giveItems(Player player, Iterable<ItemStack> items) {
        PlayerInventory inventory = player.getInventory();
        Location location = player.getLocation();
        World world = location.getWorld();
        for (ItemStack item : items) {
            ItemStack clone = item.clone();
            if (ItemUtils.hasEnoughSpace(player, clone)) {
                inventory.addItem(clone);
            } else {
                world.dropItemNaturally(location, clone);
            }
        }
    }

    public static void giveItems(Player player, ItemStack... items) {
        giveItems(player, Arrays.asList(items));
    }

    public static void combineItems(Collection<ItemStack> result, Collection<ItemStack> items) {
        for (ItemStack newItem : items) {
            boolean combined = false;
            for (ItemStack oldItem : result) {
                if (newItem.isSimilar(oldItem)) {
                    oldItem.setAmount(oldItem.getAmount() + newItem.getAmount());
                    combined = true;
                    break;
                }
            }

            if (!combined) {
                result.add(newItem);
            }
        }
    }

    public static void combineItems(Collection<ItemStack> result, ItemStack... items) {
        combineItems(result, Arrays.asList(items));
    }
}
