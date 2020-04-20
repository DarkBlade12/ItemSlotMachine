package com.darkblade12.itemslotmachine.statistic;

import java.util.HashMap;
import java.util.Map;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public enum Category {
    TOTAL_SPINS(Integer.class) {
        @Override
        public Integer parse(String s) {
            return Integer.parseInt(s);
        }

        @Override
        public String getLocalizedName(ItemSlotMachine plugin) {
            return plugin.messageManager.total_spins();
        }
    },
    WON_SPINS(Integer.class) {
        @Override
        public Integer parse(String s) {
            return Integer.parseInt(s);
        }

        @Override
        public String getLocalizedName(ItemSlotMachine plugin) {
            return plugin.messageManager.won_spins();
        }
    },
    LOST_SPINS(Integer.class) {
        @Override
        public Integer parse(String s) {
            return Integer.parseInt(s);
        }

        @Override
        public String getLocalizedName(ItemSlotMachine plugin) {
            return plugin.messageManager.lost_spins();
        }
    },
    COINS_SPENT(Integer.class) {
        @Override
        public Integer parse(String s) {
            return Integer.parseInt(s);
        }

        @Override
        public String getLocalizedName(ItemSlotMachine plugin) {
            return plugin.messageManager.coins_spent();
        }
    },
    WON_MONEY(Double.class) {
        @Override
        public Double parse(String s) {
            return Double.parseDouble(s);
        }

        @Override
        public String getLocalizedName(ItemSlotMachine plugin) {
            return plugin.messageManager.won_money();
        }
    },
    WON_ITEMS(Integer.class) {
        @Override
        public Integer parse(String s) {
            return Integer.parseInt(s);
        }

        @Override
        public String getLocalizedName(ItemSlotMachine plugin) {
            return plugin.messageManager.won_items();
        }
    };

    private static final Map<String, Category> NAME_MAP = new HashMap<String, Category>();
    private Class<? extends Number> valueType;

    static {
        for (Category c : values()) {
            NAME_MAP.put(c.name(), c);
        }
    }

    private Category(Class<? extends Number> valueType) {
        this.valueType = valueType;
    }

    public StatisticRecord createObject() {
        return new StatisticRecord(this);
    }

    public abstract Number parse(String s);

    public Class<? extends Number> getValueType() {
        return this.valueType;
    }

    public abstract String getLocalizedName(ItemSlotMachine plugin);

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