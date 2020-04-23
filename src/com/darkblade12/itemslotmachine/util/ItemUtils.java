package com.darkblade12.itemslotmachine.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemUtils {
    private ItemUtils() {}

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
}
