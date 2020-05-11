package com.darkblade12.itemslotmachine.slotmachine.combo;

import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.util.ItemUtils;

public class Action {
    protected ActionType type;

    protected Action(ActionType type) {
        this.type = type;
    }

    public static Action fromString(String text, Map<String, ItemStack> customItems) throws IllegalArgumentException {
        int separatorIndex = text.indexOf(':');
        String typeName = separatorIndex == -1 ? text : text.substring(0, separatorIndex);
        ActionType type = ActionType.fromName(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Invalid action type");
        }

        switch (type) {
            case MULTIPLY_MONEY_POT:
            case MULTIPLY_ITEM_POT:
            case RAISE_MONEY_POT:
            case PAY_OUT_MONEY:
                double amount;
                try {
                    amount = Double.parseDouble(text.substring(separatorIndex + 1));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid amount");
                }
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be higher than 0");
                }
                return new AmountAction(type, amount);
            case RAISE_ITEM_POT:
            case PAY_OUT_ITEMS:
                List<ItemStack> items = ItemUtils.listFromString(text.substring(separatorIndex + 1), customItems);
                return new ItemAction(type, items);
            case EXECUTE_COMMAND:
                String command = text.substring(separatorIndex + 1);
                if (command.startsWith("/")) {
                    if (command.length() == 1) {
                        throw new IllegalArgumentException("Invalid command");
                    }
                    command = command.substring(1);
                }
                return new CommandAction(type, command);
            case PAY_OUT_MONEY_POT:
            case PAY_OUT_ITEM_POT:
                return new Action(type);
            default:
                throw new IllegalArgumentException("Unsupported action type");
        }
    }

    public ActionType getType() {
        return type;
    }
}
