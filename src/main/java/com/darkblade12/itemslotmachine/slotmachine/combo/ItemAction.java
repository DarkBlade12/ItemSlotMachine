package com.darkblade12.itemslotmachine.slotmachine.combo;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemAction extends Action {
    private List<ItemStack> items;

    public ItemAction(ActionType type, List<ItemStack> items) {
        super(type);
        this.items = items;
    }

    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(items);
    }
}
