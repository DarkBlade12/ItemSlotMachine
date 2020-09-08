package com.darkblade12.itemslotmachine.statistic;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.PluginBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Category {
    TOTAL_SPINS(Message.STATISTIC_CATEGORY_TOTAL_SPINS, true) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    WON_SPINS(Message.STATISTIC_CATEGORY_WON_SPINS, true) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    LOST_SPINS(Message.STATISTIC_CATEGORY_LOST_SPINS, true) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    SPENT_COINS(Message.STATISTIC_CATEGORY_SPENT_COINS) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    WON_MONEY(Message.STATISTIC_CATEGORY_WON_MONEY) {
        @Override
        public Double parse(String input) {
            return Double.parseDouble(input);
        }
    },
    WON_ITEMS(Message.STATISTIC_CATEGORY_WON_ITEMS) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    };

    private static final Map<String, Category> NAME_MAP = new HashMap<>();
    private final Message message;
    private final boolean slotMachineCategory;

    static {
        for (Category category : values()) {
            String name = category.name();
            NAME_MAP.put(name, category);
            NAME_MAP.put(name.replace('_', ' '), category);
        }
    }

    Category(Message message, boolean slotMachineCategory) {
        this.message = message;
        this.slotMachineCategory = slotMachineCategory;
    }

    Category(Message message) {
        this(message, false);
    }

    public Record createRecord() {
        return new Record(this);
    }

    public abstract Number parse(String input);

    public Message getMessage() {
        return message;
    }

    public boolean isSlotMachineCategory() {
        return slotMachineCategory;
    }

    public String getLocalizedName(PluginBase plugin) {
        return plugin.formatMessage(message);
    }

    public static Category fromName(String name) {
        return NAME_MAP.getOrDefault(name.toUpperCase(), null);
    }

    public static Category fromName(ItemSlotMachine plugin, String name) {
        Category category = fromName(name);
        if (category != null) {
            return category;
        }

        return Arrays.stream(values()).filter(c -> c.getLocalizedName(plugin).equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
