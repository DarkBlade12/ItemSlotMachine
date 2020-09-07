package com.darkblade12.itemslotmachine.statistic;

import java.util.HashMap;
import java.util.Map;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.PluginBase;

public enum Category {
    TOTAL_SPINS(Integer.class, Message.STATISTIC_CATEGORY_TOTAL_SPINS) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    WON_SPINS(Integer.class, Message.STATISTIC_CATEGORY_WON_SPINS) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    LOST_SPINS(Integer.class, Message.STATISTIC_CATEGORY_LOST_SPINS) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    SPENT_COINS(Integer.class, Message.STATISTIC_CATEGORY_SPENT_COINS) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    },
    WON_MONEY(Double.class, Message.STATISTIC_CATEGORY_WON_MONEY) {
        @Override
        public Double parse(String input) {
            return Double.parseDouble(input);
        }
    },
    WON_ITEMS(Integer.class, Message.STATISTIC_CATEGORY_WON_ITEMS) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    };

    private static final Map<String, Category> NAME_MAP = new HashMap<String, Category>();
    private Class<? extends Number> valueType;
    private Message message;

    static {
        for (Category category : values()) {
            NAME_MAP.put(category.name(), category);
        }
    }

    private Category(Class<? extends Number> valueType, Message message) {
        this.valueType = valueType;
        this.message = message;
    }

    public Record createObject() {
        return new Record(this);
    }

    public abstract Number parse(String input);

    public Class<? extends Number> getValueType() {
        return this.valueType;
    }

    public Message getMessage() {
        return message;
    }

    public String getLocalizedName(PluginBase plugin) {
        return plugin.formatMessage(message);
    }

    public static Category fromName(String name) {
        return NAME_MAP.getOrDefault(name.toUpperCase(), null);
    }

    public static Category fromName(ItemSlotMachine plugin, String name) {
        Category category = fromName(name);

        if (category == null) {
            for (Category c : values()) {
                if (c.getLocalizedName(plugin).equalsIgnoreCase(name)) {
                    return c;
                }
            }

            category = fromName(name.replace(" ", "_"));
        }

        return category;
    }
}