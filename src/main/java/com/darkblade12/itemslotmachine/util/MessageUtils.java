package com.darkblade12.itemslotmachine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MessageUtils {
    private static final Map<ChatColor, ChatColor> SIMILAR_COLORS = new HashMap<>();
    private static final ChatColor[] COLORS;
    private static final Random RANDOM = new Random();

    static {
        SIMILAR_COLORS.put(ChatColor.DARK_BLUE, ChatColor.BLUE);
        SIMILAR_COLORS.put(ChatColor.DARK_GREEN, ChatColor.GREEN);
        SIMILAR_COLORS.put(ChatColor.DARK_AQUA, ChatColor.AQUA);
        SIMILAR_COLORS.put(ChatColor.DARK_RED, ChatColor.RED);
        SIMILAR_COLORS.put(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE);
        SIMILAR_COLORS.put(ChatColor.DARK_GRAY, ChatColor.GRAY);
        SIMILAR_COLORS.put(ChatColor.GOLD, ChatColor.YELLOW);

        List<ChatColor> colors = new ArrayList<>();
        for (ChatColor color : ChatColor.values()) {
            if (color.isColor() && color != ChatColor.BLACK && color != ChatColor.WHITE) {
                colors.add(color);
            }
        }
        COLORS = colors.toArray(new ChatColor[colors.size()]);
    }

    private MessageUtils() {}

    public static ChatColor randomColorCode() {
        return COLORS[RANDOM.nextInt(COLORS.length)];
    }

    public static ChatColor similarColor(ChatColor color) {
        for (Entry<ChatColor, ChatColor> entry : SIMILAR_COLORS.entrySet()) {
            ChatColor key = entry.getKey();
            ChatColor value = entry.getValue();
            if (key == color) {
                return value;
            } else if (value == color) {
                return key;
            }
        }

        return color;
    }

    public static String[] prepareSignLines(String[] lines, int... splitLines) {
        if (lines.length > 4) {
            throw new IllegalArgumentException("There cannot be more than 4 lines on a sign");
        }

        for (int index : splitLines) {
            if (index >= 0 && index < lines.length - 1) {
                String line = lines[index];
                if (line.length() > 15) {
                    String[] split = line.split(" ");
                    lines[index] = split[0];
                    lines[index + 1] = split[1];
                }
            }
        }

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            lines[i] = line.length() > 15 ? line.substring(0, 15) : line;
        }

        return lines;
    }

    public static String formatName(Enum<?> enumObj, boolean capitalize) {
        StringBuilder name = new StringBuilder();
        String[] split = enumObj.name().toLowerCase().split("_");
        for (int i = 0; i < split.length; i++) {
            if (i > 0) {
                name.append(" ");
            }
            name.append(capitalize ? StringUtils.capitalize(split[i]) : split[i]);
        }
        return name.toString();
    }

    public static String formatName(Enum<?> enumObj) {
        return formatName(enumObj, false);
    }

    public static String toString(ItemStack item) {
        String name = null;
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                name = meta.getDisplayName();
            }
        }

        if (name == null) {
            name = formatName(item.getType(), true);
        }
        return "\u00A72" + name + " \u00A78\u00D7 \u00A77" + item.getAmount();
    }

    public static String toString(List<ItemStack> items) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                text.append(ChatColor.GREEN).append(", ");
            }
            text.append(toString(items.get(i)));
        }
        return text.toString();
    }
}
