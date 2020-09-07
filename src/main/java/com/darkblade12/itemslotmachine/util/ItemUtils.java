package com.darkblade12.itemslotmachine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ItemUtils {
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder().registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        GSON = builder.create();
    }

    private ItemUtils() {
    }

    public static ItemStack fromString(String text, Map<String, ItemStack> customItems) throws IllegalArgumentException,
                                                                                               JsonParseException {
        if (text.startsWith("{") && text.endsWith("}")) {
            return GSON.fromJson(text, ItemStack.class);
        }

        String[] data = text.split("-");
        String name = data[0].toLowerCase();
        Material material = Material.matchMaterial(name);
        ItemStack item;
        if (material != null) {
            item = new ItemStack(material);
        } else if (!customItems.containsKey(name)) {
            throw new IllegalArgumentException("Invalid item name.");
        } else {
            item = customItems.get(name).clone();
        }

        if (data.length >= 2) {
            try {
                int amount = Integer.parseInt(data[1]);
                item.setAmount(amount);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount value.");
            }
        }

        return item;
    }

    public static String toString(ItemStack item, boolean withAmount) {
        StringBuilder builder = new StringBuilder(item.getType().getKey().getKey());
        if (withAmount) {
            builder.append("-").append(item.getAmount());
        }

        return builder.toString();
    }

    public static String toString(ItemStack item) {
        return toString(item, true);
    }

    public static List<ItemStack> fromListString(String text, Map<String, ItemStack> customItems) throws IllegalArgumentException {
        String[] data = text.replace(" ", "").split(",");
        List<ItemStack> items = new ArrayList<>(data.length);
        for (String item : data) {
            items.add(fromString(item, customItems));
        }

        return items;
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
            } else if (world != null) {
                world.dropItemNaturally(location, clone);
            }
        }
    }

    public static void stackItems(Collection<ItemStack> target, Collection<ItemStack> newItems) {
        for (ItemStack item : newItems) {
            ItemStack similarItem = target.stream().filter(i -> i.isSimilar(item)).findFirst().orElse(null);
            if (similarItem != null) {
                similarItem.setAmount(similarItem.getAmount() + item.getAmount());
            } else {
                target.add(item.clone());
            }
        }
    }

    public static void stackItems(Collection<ItemStack> target, ItemStack... newItems) {
        stackItems(target, Arrays.asList(newItems));
    }

    public static List<ItemStack> cloneItems(Collection<ItemStack> items) {
        List<ItemStack> cloned = new ArrayList<>(items.size());
        for (ItemStack item : items) {
            cloned.add(item.clone());
        }

        return cloned;
    }

    public static List<ItemStack> cloneItems(ItemStack... items) {
        return cloneItems(Arrays.asList(items));
    }
}
