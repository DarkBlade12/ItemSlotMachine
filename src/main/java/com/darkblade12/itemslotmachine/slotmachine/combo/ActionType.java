package com.darkblade12.itemslotmachine.slotmachine.combo;

import java.util.HashMap;
import java.util.Map;

public enum ActionType {
    MULTIPLY_MONEY_POT,
    RAISE_MONEY_POT,
    PAY_OUT_MONEY_POT,
    MULTIPLY_ITEM_POT,
    RAISE_ITEM_POT,
    PAY_OUT_ITEM_POT,
    PAY_OUT_MONEY,
    PAY_OUT_ITEMS,
    EXECUTE_COMMAND;

    private static final Map<String, ActionType> NAME_MAP = new HashMap<>();

    static {
        for (ActionType action : values()) {
            NAME_MAP.put(action.name(), action);
        }
    }

    public static ActionType fromName(String name) {
        return NAME_MAP.getOrDefault(name.toUpperCase(), null);
    }
}
